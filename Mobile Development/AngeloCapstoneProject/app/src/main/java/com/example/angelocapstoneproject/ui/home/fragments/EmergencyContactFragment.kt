package com.example.angelocapstoneproject.ui.home.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.angelocapstoneproject.R
import com.example.angelocapstoneproject.data.local.model.Contact
import com.example.angelocapstoneproject.databinding.FragmentEmergencyContactBinding
import com.example.angelocapstoneproject.domain.dummies.home.ContactDummy
import com.example.angelocapstoneproject.domain.helper.checkPermission
import com.example.angelocapstoneproject.domain.helper.obtainViewModel
import com.example.angelocapstoneproject.ui.home.Home
import com.example.angelocapstoneproject.ui.home.OnDialogContactListener
import com.example.angelocapstoneproject.ui.home.adapter.EmergencyContactAdapter
import com.example.angelocapstoneproject.ui.home.viewmodels.EmergencyContactViewModel

class EmergencyContactFragment : Fragment(), OnDialogContactListener {

    private var binding: FragmentEmergencyContactBinding?=null
    private lateinit var viewModel: EmergencyContactViewModel
    private lateinit var adapter: EmergencyContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmergencyContactBinding.inflate(
            inflater,
            container,
            false
        )
        return binding?.root
    }

/*    private val requestNotifPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted->
        if (isGranted){
            sendNotification()

            return@registerForActivityResult
        }

        Toast.makeText(
            requireActivity(),
            "Notifications permission rejected",
            Toast.LENGTH_SHORT
        ).show()
    }*/

    private val requestInsertNewContactLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){

    }

    private val requestContactPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        val contactUri = it.data?.data ?: return@registerForActivityResult
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )
        val cursor = requireActivity().contentResolver.query(
            contactUri,
            projection,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()){
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val displayNameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val number = cursor.getString(numberIndex)
            val name = cursor.getString(displayNameIndex)

            val contact = Contact(
                name = name,
                number = number
            )
            viewModel.insertContact(contact)
        }
        cursor?.close()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = obtainViewModel(requireActivity().application, this)

        binding?.apply {
            viewModel.contacts.observe(viewLifecycleOwner){
                adapter = EmergencyContactAdapter(it, ::onContactClicked)
                rvContact.adapter = adapter
            }

            fab.setOnClickListener {
                showAddContactAlertDialog()
            }

           /* btnSendNotif.setOnClickListener {
                if (Build.VERSION.SDK_INT >= 33) {
                    val permission = android.Manifest.permission.POST_NOTIFICATIONS
                    if (!checkPermission(requireActivity(), permission)){
                        requestNotifPermissionLauncher.launch(permission)
                        return@setOnClickListener
                    }
                }
                sendNotification()
            }*/
        }
    }

    private fun showAddContactAlertDialog(){
        AlertDialog.Builder(ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom))
            .setTitle("Pilih Metode")
            .setItems(
                requireActivity().resources.getStringArray(R.array.add_contact_methods)
            ){ dialog, which ->

                if (which == 0){
                    pickContact()
                }else if(which == 1){
                    toAddContactDialogFragment()
                }
                dialog.dismiss()
            }.create()
            .show()
    }

    private fun toAddContactDialogFragment(){
        val fragment = DetailAddContactDialogFragment()
        fragment.show(childFragmentManager, null)
    }

    private fun pickContact(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        requestContactPickerLauncher.launch(intent)
    }

    private fun onContactClicked(contact: Contact){
        val fragment = DetailAddContactDialogFragment()
        val args = Bundle().apply {
            putLong(DetailAddContactDialogFragment.CONTACT_ID_KEY, contact.id)
            putString(DetailAddContactDialogFragment.CONTACT_NAME_KEY, contact.name)
            putString(DetailAddContactDialogFragment.CONTACT_NUMBER_KEY, contact.number)
        }
        fragment.arguments = args
        fragment.show(childFragmentManager, null)
    }

  /*  @SuppressLint("ObsoleteSdkInt")
    private fun sendNotification(){
        val bitmapLargeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_alert)

        val pendingFlags:Int = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            PendingIntent.FLAG_IMMUTABLE
        }else 0
        val intentToPlayback = Intent(requireActivity(), Home::class.java).also { it.action = EMERGENCY_NOTIF_ACTION }
        val pendingIntentPlayback = PendingIntent.getActivity(requireActivity(), 0, intentToPlayback, pendingFlags)

        val intentEmergencyCall = Intent(Intent.ACTION_CALL)
        intentEmergencyCall.data = Uri.parse("tel:911")
        val pendingIntentEmergencyCall = PendingIntent.getActivity(requireActivity(), 0, intentEmergencyCall, pendingFlags)

        val mNotificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(requireActivity(), "test")

        val callAction = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(requireActivity().applicationContext, R.drawable.logo),
            "Emergency Call",
            pendingIntentEmergencyCall
        ).build()
        notificationBuilder
            .setContentTitle("There's a fall accident!!")
            .setContentInfo("Cam Gudang")
            .setContentText("A fall accident detected in Cam Gudang")
            .setSmallIcon(R.drawable.logo)
            .setDefaults(Notification.DEFAULT_SOUND.or(Notification.DEFAULT_VIBRATE))
            .setAutoCancel(true)
            .setLargeIcon(bitmapLargeIcon)
            .setContentIntent(pendingIntentPlayback)
            .addAction(callAction)
            .also { it.priority = NotificationCompat.PRIORITY_HIGH }
            .build()


        val notification = notificationBuilder.build()
        mNotificationManager.notify(NOTIF_ID, notification)
    }*/

    override fun onDeleteContact(selectedContact: Contact) {
        viewModel.deleteContact(selectedContact)
    }

    override fun onAddContact(newContact: Contact) {
        insertNewContactToSystemApp(newContact)
        viewModel.insertContact(newContact)
    }

    private fun insertNewContactToSystemApp(newContact: Contact){
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, newContact.name)
            putExtra(ContactsContract.Intents.Insert.PHONE, newContact.number)
        }
        requestInsertNewContactLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object{
        private const val NOTIF_ID = 100

        const val EMERGENCY_NOTIF_ACTION = "emergency action"
        const val EMERGENCY_CALL_ACTION = "call_action"
    }

}