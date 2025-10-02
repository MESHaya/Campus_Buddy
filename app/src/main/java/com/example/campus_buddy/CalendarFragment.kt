package com.example.campus_buddy

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campus_buddy.data.LocalEvent
import com.example.campus_buddy.databse.DatabaseHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes

import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.concurrent.thread

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: EventAdapter
    private lateinit var googleSignInClient: GoogleSignInClient

    private var selectedDate: String = ""
    private val RC_SIGN_IN = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        db = DatabaseHelper(requireContext())
        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.recyclerViewEvents)
        fabAdd = view.findViewById(R.id.fabAddEvent)

        adapter = EventAdapter(emptyList()) { event ->
            showEventOptions(event)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Default date = today
        selectedDate =
            android.text.format.DateFormat.format("yyyy-MM-dd", java.util.Date()).toString()
        loadEvents(selectedDate)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            loadEvents(selectedDate)
        }

        fabAdd.setOnClickListener { showAddEventDialog() }

        setupGoogleSignIn()

        return view
    }

    // --- GOOGLE SIGN IN ---
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(CalendarScopes.CALENDAR))
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account == null) {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        } else {
            fetchGoogleCalendarEvents(account)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(Exception::class.java)
                if (account != null) {
                    Toast.makeText(
                        requireContext(),
                        "Signed in as ${account.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchGoogleCalendarEvents(account)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Sign-in failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // --- FETCH GOOGLE EVENTS ---
    private fun fetchGoogleCalendarEvents(account: GoogleSignInAccount) {
        thread {
            try {
                val credential: GoogleAccountCredential = GoogleAccountCredential
                    .usingOAuth2(requireContext(), listOf(CalendarScopes.CALENDAR))
                credential.selectedAccount = account.account

                val service = Calendar.Builder(
                    NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("CampusBuddy").build()

                val events = service.events().list("primary")
                    .setMaxResults(20)
                    .execute()
                    .items

                val googleEvents = events.map { ev ->
                    UnifiedEvent(
                        id = ev.id ?: "0",
                        title = ev.summary ?: "No Title",
                        time = ev.start?.dateTime?.toString() ?: "No time",
                        description = ev.description ?: "",
                        isGoogleEvent = true
                    )
                }

                requireActivity().runOnUiThread {
                    val localEvents = db.getEventsByDate(selectedDate).map { local ->
                        UnifiedEvent(
                            id = local.id.toString(),
                            title = local.title,
                            time = local.time,
                            description = local.description,
                            isGoogleEvent = false
                        )
                    }

                    val combined = localEvents + googleEvents
                    adapter.updateEvents(combined)
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error fetching Google events: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // --- LOAD LOCAL EVENTS ---
    private fun loadEvents(date: String) {
        val localEvents = db.getEventsByDate(date).map { local ->
            UnifiedEvent(
                id = local.id.toString(),
                title = local.title,
                time = local.time,
                description = local.description,
                isGoogleEvent = false
            )
        }
        adapter.updateEvents(localEvents) // Google events will be appended async
    }

    // --- ADD EVENT DIALOG ---
    private fun showAddEventDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Event")

        val layout = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val etTitle = layout.findViewById<EditText>(R.id.etEventTitle)
        val etTime = layout.findViewById<EditText>(R.id.etEventTime)
        val etDescription = layout.findViewById<EditText>(R.id.etEventDescription)

        builder.setView(layout)
        builder.setPositiveButton("Save") { _, _ ->
            val title = etTitle.text.toString()
            val time = etTime.text.toString()
            val desc = etDescription.text.toString()

            if (title.isNotEmpty()) {
                db.insertEvent(title, selectedDate, time, desc)
                loadEvents(selectedDate)
                Toast.makeText(requireContext(), "Event added", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // --- EVENT OPTIONS ---
    private fun showEventOptions(event: UnifiedEvent) {
        if (event.isGoogleEvent) {
            Toast.makeText(
                requireContext(),
                "Google events can’t be edited locally.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(requireContext())
            .setTitle(event.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditEventDialog(event)
                    1 -> {
                        db.deleteEvent(event.id.toInt())
                        loadEvents(selectedDate)
                        Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }.show()
    }

    private fun showEditEventDialog(event: UnifiedEvent) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Event")

        val layout = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val etTitle = layout.findViewById<EditText>(R.id.etEventTitle)
        val etTime = layout.findViewById<EditText>(R.id.etEventTime)
        val etDescription = layout.findViewById<EditText>(R.id.etEventDescription)

        etTitle.setText(event.title)
        etTime.setText(event.time)
        etDescription.setText(event.description)

        builder.setView(layout)
        builder.setPositiveButton("Update") { _, _ ->
            val updatedEvent = LocalEvent(
                id = event.id.toInt().toString(),
                title = etTitle.text.toString(),
                date = selectedDate,
                time = etTime.text.toString(),
                description = etDescription.text.toString()
            )
            db.updateEvent(updatedEvent)
            loadEvents(selectedDate)
            Toast.makeText(requireContext(), "Event updated", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}
