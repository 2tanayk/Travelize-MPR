package com.myapp.travelize.main.mainscreen

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.myapp.travelize.R
import com.myapp.travelize.adapters.MessageAdapter
import com.myapp.travelize.adapters.MessageAdapter.Companion.IMG_MESSAGE
import com.myapp.travelize.adapters.MessageAdapter.Companion.TEXT_MESSAGE
import com.myapp.travelize.authentication.MainActivity.Companion.FIRESTORE_SHARED_PREF
import com.myapp.travelize.authentication.MainActivity.Companion.USER_NAME
import com.myapp.travelize.main.MainHostActivity2.Companion.CHAT_DOC_REF
import com.myapp.travelize.main.MainHostActivity2.Companion.CHAT_GROUP_KEY
import com.myapp.travelize.main.ProfileDialogFragment
import com.myapp.travelize.models.Message
import java.io.File


class ChatFragment : Fragment(), MessageAdapter.OnItemClickListener {
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    val userDocRef = db.collection("Users").document(firebaseAuth.getCurrentUser().getUid())

    lateinit var chatDocumentRefString: String
    lateinit var messagesRecyclerView: RecyclerView
    lateinit var messagesAdapter: MessageAdapter
    lateinit var messagesView: View
    lateinit var chatBoxEditText: EditText
    lateinit var attachmentIconImgView: ImageView
    lateinit var clickIconImgView: ImageView
    lateinit var sendMessageImgView: ImageView
    lateinit var sharedPref: SharedPreferences
    lateinit var msgCollectionRef: CollectionReference
    lateinit var msgsProgressBar: ProgressBar
    lateinit var tempFile: File
    lateinit var tempUri: Uri
    lateinit var sentImageUrl: String
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageRef = storage.reference


    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.e("Permission", "Granted")
            } else {
                Log.e("Permission", "Denied")
            }
        }

    val getCapturedImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                Log.e("callback", "image uri saved! " + tempUri.toString())
                saveImageToStorage()
            } else {
                Log.e("callback", "failed!")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messagesView = view
        sharedPref = requireActivity().getSharedPreferences(FIRESTORE_SHARED_PREF, MODE_PRIVATE)
        msgsProgressBar = view.findViewById(R.id.msgs_progress_bar)
        chatBoxEditText = view.findViewById(R.id.type_msg_edit_txt)
        attachmentIconImgView = view.findViewById(R.id.attachment_img_view)
        clickIconImgView = view.findViewById(R.id.click_pic_img_view)
        sendMessageImgView = view.findViewById(R.id.send_msg_img_view)

        chatBoxEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val msg: String = chatBoxEditText.text.toString().trim()
                if (msg.isNotEmpty() && attachmentIconImgView.isVisible && clickIconImgView.isVisible) {
                    attachmentIconImgView.visibility = View.GONE
                    clickIconImgView.visibility = View.GONE
                    sendMessageImgView.visibility = View.VISIBLE
                }
                if (msg.isEmpty() && !attachmentIconImgView.isVisible && !clickIconImgView.isVisible) {
                    sendMessageImgView.visibility = View.GONE
                    attachmentIconImgView.visibility = View.VISIBLE
                    clickIconImgView.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        clickIconImgView.setOnClickListener {
            Log.e("ImageIcon", "clicked!")
            if (!hasCameraPermission()) {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            if (hasCameraPermission()) {
                captureImageForChat()
            }
        }

        val bundle = arguments
        if (bundle != null) {
            Log.e("chat", "${bundle.getInt(CHAT_GROUP_KEY, -1)}")
            val tempDocRef = bundle.getString(CHAT_DOC_REF, null)
            if (tempDocRef != null) {
                Log.e("document ref string", tempDocRef)
                chatDocumentRefString = tempDocRef
                msgCollectionRef = db.document(chatDocumentRefString).collection("messages")
                setUpMessagesRecyclerView(chatDocumentRefString)
                sendUserTextMessage()
            } else {
                Log.e("chat doc ref", "is null")
            }
        } else {
            Log.e("bundle", "is null")
        }
    }

    private fun saveImageToStorage() {
        val storageRef = storageRef.child(firebaseAuth.currentUser.uid + "/images/")
            .child("${System.currentTimeMillis()}.png")

        storageRef.putFile(tempUri).addOnSuccessListener {
            Log.e("Image", "uploaded successfully")

            storageRef.downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    sentImageUrl = it.result.toString()
                    sendUserImageMessage(sentImageUrl)
                } else {
                    Log.e("download url", "fetch failed :(")
                }
            }
        }
    }

    private fun captureImageForChat() {
        tempFile = File(requireActivity().filesDir, "${System.currentTimeMillis()}.png")
        tempUri =
            FileProvider.getUriForFile(requireActivity(), "com.myapp.travelize", tempFile)
        try {
            getCapturedImage.launch(tempUri)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            e.printStackTrace()
        }
    }

    private fun sendUserImageMessage(sentImageUrl: String) {
        val userUid = firebaseAuth.getCurrentUser().getUid()
        val msgType = IMG_MESSAGE
        var userName = sharedPref.getString(USER_NAME, null)
        var message: Message

        if (userName == null) {
            userDocRef.get().addOnSuccessListener {
                userName = it.getString("name")
                message = Message(userName!!, userUid, sentImageUrl, msgType)

                msgCollectionRef.add(message).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.e("Image", "sent!")
                        scrollToLastMsg()
                    } else {
                        Log.e("Message", "failed to deliver :(")
                    }
                }//addOnCompleteListener ends
                //caching the user's name
                val editor = sharedPref.edit()
                editor.putString(USER_NAME, userName)
                editor.apply()
            }.addOnFailureListener {
                Log.e("name fetch", it.toString())
                it.printStackTrace()
            }//addOnFailureListener
        } else {
            message = Message(userName!!, userUid, sentImageUrl, msgType)

            msgCollectionRef.add(message).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.e("Image", "sent!")
                    scrollToLastMsg()
                } else {
                    Log.e("Message", "failed to deliver :(")
                }//inner else ends
            }//addOnCompleteListener ends
        }
    }

    private fun setUpMessagesRecyclerView(chatDocumentRefString: String) {
        Log.e("Your chat", "retrieving msgs")
        val query = db.document(chatDocumentRefString).collection("messages").orderBy("sent")
        val options: FirestoreRecyclerOptions<Message> = FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query, Message::class.java)
            .build()
        messagesAdapter = MessageAdapter(options, this)
        messagesRecyclerView = messagesView.findViewById(R.id.msgs_recycler_view)
//        messagesRecyclerView.setHasFixedSize(true)
        messagesRecyclerView.adapter = messagesAdapter
        msgsProgressBar.visibility = View.GONE
    }

    private fun sendUserTextMessage() {
        sendMessageImgView.setOnClickListener {
            val userMsg = chatBoxEditText.text.toString().trim()
            val userUid = firebaseAuth.getCurrentUser().getUid()
            val msgType = TEXT_MESSAGE
            var userName = sharedPref.getString(USER_NAME, null)
            var message: Message
            if (userName == null) {
                userDocRef.get().addOnSuccessListener {
                    userName = it.getString("name")
                    message = Message(userName!!, userUid, userMsg, msgType)
                    msgCollectionRef.add(message).addOnCompleteListener {
                        if (it.isSuccessful) {
                            chatBoxEditText.text.clear()
                            Log.e("Message", "sent!")
                            scrollToLastMsg()
                        } else {
                            Log.e("Message", "failed to deliver :(")
                        }
                    }//addOnCompleteListener ends
                    //caching the user's name
                    val editor = sharedPref.edit()
                    editor.putString(USER_NAME, userName)
                    editor.apply()
                }.addOnFailureListener {
                    Log.e("name fetch", it.toString())
                    it.printStackTrace()
                }//addOnFailureListener
            } else {
                message = Message(userName!!, userUid, userMsg, TEXT_MESSAGE)
                msgCollectionRef.add(message).addOnCompleteListener {
                    if (it.isSuccessful) {
                        chatBoxEditText.text.clear()
                        Log.e("Message", "sent!")
                        scrollToLastMsg()
                    } else {
                        Log.e("Message", "failed to deliver :(")
                    }//inner else ends
                }//addOnCompleteListener ends
            }//outer else ends
        }//Send btn click listener ends
    }//sendUserMessage ends

    private fun scrollToLastMsg() {
        Handler(Looper.getMainLooper()).postDelayed({
            messagesRecyclerView.scrollToPosition(messagesRecyclerView.adapter?.itemCount!! - 1)
        }, 500)
    }

    override fun onStart() {
        super.onStart()
        messagesAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        messagesAdapter.stopListening()
    }

    override fun onMessageClick(position: Int, item: Message) {
        Log.e("Chat", item.toString())
        val dialog = ProfileDialogFragment()

        val args = Bundle()

        db.collection("Users").document(item.senderUid + "").get().addOnSuccessListener {
            Log.e("doc data", it.toString())
            args.putString("name", it.getString("name")!!.trim())
            args.putString("gender", it.getString("gender")!!.trim())
            args.putString("description", it.getString("description") ?: "")
            args.putString("imageURL", it.getString("imageURL") ?: "")
            args.putString("institute", it.getString("insitute") ?: "")
            args.putString("dob", it.getString("dob"))
            args.putStringArrayList("passions", it.get("passions") as ArrayList<String>)

            dialog.arguments = args
            dialog.show(requireActivity().supportFragmentManager, "profileDialog")
        }

    }

    fun hasCameraPermission() =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
}