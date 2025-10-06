package com.example.campus_buddy

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campus_buddy.databse.DatabaseHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.client.util.DateTime
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var db: DatabaseHelper
    private lateinit var googleSignInClient: GoogleSignInClient

    private var selectedDate: String = ""
    private var currentAccount: GoogleSignInAccount? = null
    private var cachedEvents: List<UnifiedEvent> = emptyList()

    companion object {
        private const val TAG = "CalendarFragment"
    }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)

            currentAccount = account
            Log.d(TAG, "Sign-in successful: ${account.email}")

            Toast.makeText(
                requireContext(),
                "Signed in as ${account.email}",
                Toast.LENGTH_SHORT
            ).show()

            fetchGoogleCalendarEvents(account)
        } catch (e: ApiException) {
            Log.e(TAG, "Sign-in failed: ${e.statusCode}", e)
            Toast.makeText(
                requireContext(),
                "Sign-in failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in error", e)
            Toast.makeText(
                requireContext(),
                "Error: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        db = DatabaseHelper(requireContext())
        calendarView = view.findViewById(R.id.calendarView)
        fabAdd = view.findViewById(R.id.fabAddEvent)

        // Default date = today
        selectedDate =
            android.text.format.DateFormat.format("yyyy-MM-dd", java.util.Date()).toString()

        loadEvents(selectedDate)

        // Show popup when date is clicked
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            loadEvents(selectedDate)
            showEventsPopup(selectedDate)
        }

        fabAdd.setOnClickListener { showAddEventDialog() }

        setupGoogleSignIn()

        return view
    }

    // --- SHOW EVENTS POPUP ---
    private fun showEventsPopup(date: String) {
        val eventsForDate = cachedEvents.filter { event ->
            val eventTime = event.time ?: ""

            // For local events, compare dates directly
            if (!event.isGoogleEvent) {
                eventTime.startsWith(date) || date == selectedDate
            } else {
                // For Google events, parse the date from the time string
                try {
                    if (eventTime.length >= 10) {
                        val eventDate = eventTime.substring(0, 10)
                        eventDate == date
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    false
                }
            }
        }

        if (eventsForDate.isEmpty()) {
            Toast.makeText(requireContext(), "No events on this date", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a custom dialog with RecyclerView
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_events_list, null)

        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewEventsPopup)
        val adapter = EventAdapter(eventsForDate) { event ->
            showEventOptions(event)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val displayDate = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(sdf.parse(date) ?: Date())
        } catch (e: Exception) {
            date
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Events on $displayDate")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .setNeutralButton("Add Event") { _, _ ->
                showAddEventDialog()
            }
            .show()
    }

    // --- GOOGLE SIGN IN ---
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(CalendarScopes.CALENDAR))
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        val account = GoogleSignIn.getLastSignedInAccount(requireContext())

        if (account != null && GoogleSignIn.hasPermissions(account, Scope(CalendarScopes.CALENDAR))) {
            currentAccount = account
            Log.d(TAG, "Already signed in: ${account.email}")
            Toast.makeText(requireContext(), "Connected to ${account.email}", Toast.LENGTH_SHORT).show()
            fetchGoogleCalendarEvents(account)

        } else {
            Log.d(TAG, "Not signed in, prompting user")
            Toast.makeText(requireContext(), "Please sign in to sync with Google Calendar", Toast.LENGTH_LONG).show()
            signInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    // --- FETCH GOOGLE EVENTS ---
    private fun fetchGoogleCalendarEvents(account: GoogleSignInAccount) {
        thread {
            try {
                val credential = GoogleAccountCredential
                    .usingOAuth2(requireContext(), listOf(CalendarScopes.CALENDAR))
                credential.selectedAccount = account.account

                val service = Calendar.Builder(
                    NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("CampusBuddy").build()

                val events = service.events().list("primary")
                    .setMaxResults(100)
                    .execute()
                    .items

                Log.d(TAG, "Fetched ${events.size} Google events")

                val googleEvents = events.map { ev ->
                    UnifiedEvent(
                        id = ev.id ?: "0",
                        title = ev.summary ?: "No Title",
                        time = ev.start?.dateTime?.toString() ?: ev.start?.date?.toString() ?: "No time",
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

                    cachedEvents = localEvents + googleEvents
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching Google events", e)
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

    // --- ADD EVENT TO GOOGLE CALENDAR ---
    private fun addEventToGoogleCalendar(title: String, date: String, time: String, description: String) {
        val account = currentAccount

        if (account == null) {
            Log.w(TAG, "currentAccount is null, prompting sign-in")
            Toast.makeText(requireContext(), "Please sign in to Google Calendar", Toast.LENGTH_LONG).show()
            signInLauncher.launch(googleSignInClient.signInIntent)
            return
        }

        Log.d(TAG, "Adding event to Google Calendar for account: ${account.email}")

        thread {
            try {
                val credential = GoogleAccountCredential
                    .usingOAuth2(requireContext(), listOf(CalendarScopes.CALENDAR))
                credential.selectedAccount = account.account

                val service = Calendar.Builder(
                    NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("CampusBuddy").build()

                val dateTimeString = "$date ${time.ifEmpty { "12:00" }}"
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val startDate = sdf.parse(dateTimeString) ?: Date()
                val endDate = Date(startDate.time + 3600000)

                val event = Event().apply {
                    summary = title
                    this.description = description
                    start = EventDateTime().apply {
                        dateTime = DateTime(startDate)
                        timeZone = TimeZone.getDefault().id
                    }
                    end = EventDateTime().apply {
                        dateTime = DateTime(endDate)
                        timeZone = TimeZone.getDefault().id
                    }
                }

                val createdEvent = service.events().insert("primary", event).execute()
                Log.d(TAG, "Event created: ${createdEvent.id}")

                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Event added to Google Calendar ✅", Toast.LENGTH_SHORT).show()
                    fetchGoogleCalendarEvents(account)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding event to Google Calendar", e)
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Failed to add to Google Calendar: ${e.message}",
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

        cachedEvents = localEvents

        currentAccount?.let {
            Log.d(TAG, "Refreshing Google events")
            fetchGoogleCalendarEvents(it)
        } ?: Log.d(TAG, "Not signed in, showing only local events")
    }

    // --- ADD EVENT DIALOG ---
    private fun showAddEventDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Event")

        val layout = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val etTitle = layout.findViewById<EditText>(R.id.etEventTitle)
        val etTime = layout.findViewById<EditText>(R.id.etEventTime)
        val etDescription = layout.findViewById<EditText>(R.id.etEventDescription)

        //etTime.hint = "Time (HH:mm, e.g., 14:30)"

        builder.setView(layout)
        builder.setPositiveButton("Save") { _, _ ->
            val title = etTitle.text.toString()
            val time = etTime.text.toString()
            val desc = etDescription.text.toString()

            if (title.isNotEmpty()) {
                db.insertEvent(title, selectedDate, time, desc)
                Toast.makeText(requireContext(), "Event saved locally", Toast.LENGTH_SHORT).show()

                if (currentAccount != null) {
                    addEventToGoogleCalendar(title, selectedDate, time, desc)
                } else {
                    Toast.makeText(requireContext(), "Sign in to sync with Google Calendar", Toast.LENGTH_SHORT).show()
                }

                loadEvents(selectedDate)
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
                "Google events can't be edited from this app.",
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
            val updatedEvent = Event(
                id = event.id.toInt(),
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