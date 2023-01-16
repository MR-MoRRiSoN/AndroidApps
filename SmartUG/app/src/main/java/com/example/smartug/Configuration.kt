package com.example.smartug

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class Configuration : AppCompatActivity() {

    private val itemMode = arrayOf("Winter", "Summer", "None")
    private val itemRule = arrayOf("Costume Rule", "For All Device", "For This Device Only")
    private val itemPowerMode = arrayOf("ON", "OFF", "None")

    private lateinit var firebaseDatabase: FirebaseDatabase


    private lateinit var autoCompleteTxtChooseRuleType: AutoCompleteTextView
    private lateinit var autoCompleteTxtChooseMode: AutoCompleteTextView
    private lateinit var autoCompleteTxtPowerMode: AutoCompleteTextView
    private lateinit var arrayAdapterChooseRuleType: ArrayAdapter<String>
    private lateinit var arrayAdapterChooseMode: ArrayAdapter<String>
    private lateinit var arrayAdapterPowerMode: ArrayAdapter<String>
    private lateinit var linerLayout: LinearLayout
    private lateinit var linerLayoutControler: LinearLayout
    private lateinit var linerLayoutRemote: LinearLayout

    private lateinit var submitButton: Button
    private lateinit var btnOnOffConditioner: ImageView
    private lateinit var btnOnOffProjector: ImageView
    private lateinit var timeOnHourse: EditText
    private lateinit var timeOnMinute: EditText
    private lateinit var timeOFFHourse: EditText
    private lateinit var timeOFFMinute: EditText
    private lateinit var onOfTemperature: EditText
    private lateinit var btnChangeModeConditioner: ImageView
    private lateinit var btnChangeModeProjector: ImageView
    private lateinit var costumeFloor: TextView
    private lateinit var costumeRoom: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

/*        val actionBar: ActionBar? = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#ed1b24"))
        actionBar?.setBackgroundDrawable(colorDrawable)*/

        autoCompleteTxtChooseRuleType = findViewById(R.id.ruleType)
        autoCompleteTxtChooseMode = findViewById(R.id.modType)
        autoCompleteTxtPowerMode = findViewById(R.id.device_on_off)
        arrayAdapterChooseRuleType = ArrayAdapter(this, R.layout.list_item, itemMode)
        arrayAdapterChooseMode = ArrayAdapter(this, R.layout.list_item, itemRule)
        arrayAdapterPowerMode = ArrayAdapter(this, R.layout.list_item, itemPowerMode)

        autoCompleteTxtChooseRuleType.setAdapter(arrayAdapterChooseRuleType)
        autoCompleteTxtChooseMode.setAdapter(arrayAdapterChooseMode)
        autoCompleteTxtPowerMode.setAdapter(arrayAdapterPowerMode)
        firebaseDatabase = FirebaseDatabase.getInstance()

        onClickListener()
        getTempAndHum()
    }


    private fun onClickListener() {
        linerLayout = findViewById(R.id.CostumeRuleLinerLayout)
        linerLayoutControler = findViewById(R.id.linerLayoutControler)
        linerLayoutRemote = findViewById(R.id.conditionerMode)
        submitButton = findViewById(R.id.submitButton)
        btnOnOffConditioner = findViewById(R.id.btn_onOff)
        btnOnOffProjector = findViewById(R.id.btn_onOffProjector)

        timeOnHourse = findViewById(R.id.timeOnHourse)
        timeOnMinute = findViewById(R.id.timeOnMinute)

        timeOFFHourse = findViewById(R.id.timeOffHourse)
        timeOFFMinute = findViewById(R.id.timeOffMinute)
        onOfTemperature = findViewById(R.id.onOfTemperature)

        btnChangeModeConditioner = findViewById(R.id.btn_changeMode)
        btnChangeModeProjector = findViewById(R.id.btn_changeModeProjector)


        var inputRuleMode = "null"
        var inputRuleType = "null"
        var inputPowerMode = "null"
        val offTemperature: TextView = findViewById(R.id.offTemperature)
        val chooseRuleType: TextInputLayout = findViewById(R.id.chooseRuleType)
        val linerLayoutProjector: LinearLayout = findViewById(R.id.projectorMode)
        val celsius: TextView = findViewById(R.id.celsius)
        btnOnOffConditioner.setOnClickListener {
            checkConditionOfDevice("Conditioner")
        }
        btnOnOffProjector.setOnClickListener {
            println("Click")
            checkConditionOfDevice("Projector")

        }
        btnChangeModeProjector.setOnClickListener {
            celsius.visibility = View.GONE
            onOfTemperature.visibility = View.GONE
            linerLayoutProjector.visibility = View.GONE
            offTemperature.visibility = View.GONE
            linerLayoutRemote.visibility = View.GONE
            chooseRuleType.visibility = View.GONE
            linerLayoutControler.visibility = View.VISIBLE
        }


        btnChangeModeConditioner.setOnClickListener {
            linerLayoutRemote.visibility = View.GONE
            linerLayoutControler.visibility = View.VISIBLE
        }

        autoCompleteTxtChooseMode.onItemClickListener =
            OnItemClickListener { parent, _, position, _ ->
                val item = parent.getItemAtPosition(position).toString()
                inputRuleMode = item
                if (item == "Costume Rule") {
                    alertDialogueFloor()
                } else {
                    linerLayout.visibility = View.GONE
                }
            }
        autoCompleteTxtChooseRuleType.onItemClickListener =
            OnItemClickListener { parent, _, position, _ ->
                val item = parent.getItemAtPosition(position).toString()
                inputRuleType = item
            }
        autoCompleteTxtPowerMode.onItemClickListener =
            OnItemClickListener { parent, _, position, _ ->
                val item = parent.getItemAtPosition(position).toString()
                inputPowerMode = item
            }
        submitButton.setOnClickListener {
            submitRule(
                "${timeOnHourse.text}:${timeOnMinute.text}",
                "${timeOFFHourse.text}:${timeOFFMinute.text}",
                inputRuleMode,
                inputRuleType,
                inputPowerMode,
                "${onOfTemperature.text}",
                celsius.isVisible
            )
            intent = Intent(this@Configuration, Configuration::class.java)
            startActivity(intent)
        }
    }

    private fun alertDialogueFloor() {
        val countFloor = getInfoAboutRoomAndFloor(2)
        val arrayString = Array(countFloor) { "" }
        for (i in 0 until countFloor) {
            arrayString[i] = "Floor_N$i"
        }

        val mBuilder = AlertDialog.Builder(this@Configuration)
        mBuilder.setTitle("Choose Flour")
        mBuilder.setSingleChoiceItems(arrayString, -1) { dialogInterface, i ->
            alertDialogueRoom(i)
            dialogInterface.dismiss()
        }

        val mDialog = mBuilder.create()
        mDialog.show()

    }

    private fun alertDialogueRoom(floor: Int) {
        costumeFloor = findViewById(R.id.costumFlour)
        costumeRoom = findViewById(R.id.costumeRoom)
        firebaseDatabase.reference.child("ListOfDevice").child("Floor_$floor")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val countRoom = snapshot.children.count()
                    val arrayString = Array(countRoom + 1) { "" }
                    val selectedList = ArrayList<Int>()
                  //  val selectedListBoolean = mutableListOf<Boolean>(countRoom + 1)
                    val builder = AlertDialog.Builder(this@Configuration)
                    builder.setTitle("Choose Flour")
                    for (t in 0..countRoom) {
                    //    selectedListBoolean[t]=false
                        if (t == 0) {
                            arrayString[t] = "Select All"
                        } else {
                            arrayString[t] = "Room_$t"
                        }

                    }

                    builder.setMultiChoiceItems(arrayString,null) { _, which, isChecked ->
                        if (isChecked) {
                            if (which==0){
                                for (i in arrayString.indices){
                                   selectedList.add(i)

                                }
                                println(selectedList)

                            }else{
                            selectedList.add(which)
                            }
                        } else if (selectedList.contains(which)) {
                            selectedList.remove(Integer.valueOf(which))
                        }

                    }

                    builder.setNegativeButton("Submit") { _, _ ->
                        linerLayout.visibility = View.VISIBLE
                        var roomString = "${costumeRoom.text}"
                        var floorString = "${costumeFloor.text}"
                        var check = false
                        if (floorString != floor.toString() && floorString != "") {
                            floorString += "&$floor"
                            roomString += "&"
                            check = true
                        } else {
                            floorString = "$floor"
                        }
                        var test = roomString
                        for (i in 0 until selectedList.size) {
                            if (roomString == "") {
                                test += if (i < selectedList.size - 1) {
                                    "${selectedList[i]}@"
                                } else {
                                    "${selectedList[i]}"
                                }
                            } else {
                                if (check) {
                                    test += "${selectedList[i]}"
                                    check = false

                                } else {
                                    test += "@${selectedList[i]}"
                                }
                            }
                        }
                        costumeRoom.text = test
                        costumeFloor.text = floorString
                    }
                    builder.setPositiveButton("Add Rooms") { _, _ ->
                        var roomString = "${costumeRoom.text}"
                        var floorString = "${costumeFloor.text}"
                        var check = false
                        if (floorString != floor.toString() && floorString != "") {
                            floorString += "&$floor"
                            roomString += "&"
                            check = true
                        } else {
                            floorString = "$floor"
                        }

                        var test = roomString
                        for (i in 0 until selectedList.size) {
                            if (roomString == "") {
                                test += if (i < selectedList.size - 1) {
                                    "${selectedList[i]}@"
                                } else {
                                    "${selectedList[i]}"
                                }
                            } else {
                                if (check) {
                                    test += "${selectedList[i]}"
                                    check = false

                                } else {
                                    test += "@${selectedList[i]}"
                                }
                            }
                        }
                        costumeRoom.text = test
                        costumeFloor.text = floorString
                        alertDialogueFloor()
                    }

                    builder.show()

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


    }

    private fun submitRule(
        timeOn: String,
        timOff: String,
        inputRuleMode: String,
        inputRuleType: String,
        inputPowerMode: String,
        onOfTemperature: String,
        check: Boolean
    ) {
        costumeFloor = findViewById(R.id.costumFlour)
        costumeRoom = findViewById(R.id.costumeRoom)
        val address: String = if (check) {
            "Conditioner"
        } else {
            "Projector"
        }



        if (inputRuleMode == "Costume Rule") {
            val floors = costumeFloor.text.toString()
            var rooms = costumeRoom.text.toString()
            val arrayFlor = floors.split("&")
            first@ for (f in arrayFlor.indices) {
                second@ for (r in rooms.indices) {
                    if (rooms.substring(r, r + 1) == "&") {
                        println("------------------------------")
                        rooms = rooms.substring(r + 1, rooms.length)
                        println("------------------------------")
                        break@second
                    } else {
                        if (rooms.substring(r, r + 1) != "@" &&rooms.substring(r, r + 1) != "0") {
                            val room = rooms.substring(r, r + 1)
                            firebaseDatabase.reference.child("ListOfDevice")
                                .child("Floor_${arrayFlor[f]}").child(room).child(address)
                                .child("rule").child("inputRuleType")
                                .setValue(inputRuleType)

                            firebaseDatabase.reference.child("ListOfDevice")
                                .child("Floor_${arrayFlor[f]}").child(room).child(address)
                                .child("rule").child("inputPowerModeManual")
                                .setValue(inputPowerMode)

                            firebaseDatabase.reference.child("ListOfDevice")
                                .child("Floor_${arrayFlor[f]}").child(room).child(address)
                                .child("rule").child("timeOn").setValue(timeOn)

                            firebaseDatabase.reference.child("ListOfDevice")
                                .child("Floor_${arrayFlor[f]}").child(room).child(address)
                                .child("rule").child("timOff").setValue(timOff)
                            firebaseDatabase.reference.child("ListOfDevice")
                                .child("Floor_${arrayFlor[f]}").child(room).child(address)
                                .child("rule").child("onOfTemperature")
                                .setValue(onOfTemperature)
                            println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
                            if (rooms.length == 1)
                                break
                        }
                    }
                }
            }


        } else {
            if (inputRuleMode == "For All Device") {

                val countFloor = getInfoAboutRoomAndFloor(2)
                for (i in 0 until countFloor) {
                    // countInBase(i)
                    //val countRoom = getInfoAboutRoomAndFloor(1)
                    //  println("$$$$$$$$$$$$$$$$$$countRoom")

                    firebaseDatabase.reference.child("ListOfDevice").child("Floor_$i")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val countRoom = snapshot.children.count()
                                for (t in 1..countRoom) {
                                    //      if (inputRuleType!="null")
                                    firebaseDatabase.reference.child("ListOfDevice")
                                        .child("Floor_$i").child(t.toString()).child(address)
                                        .child("rule").child("inputRuleType")
                                        .setValue(inputRuleType)

                                    firebaseDatabase.reference.child("ListOfDevice")
                                        .child("Floor_$i").child(t.toString()).child(address)
                                        .child("rule").child("inputPowerModeManual")
                                        .setValue(inputPowerMode)

                                    firebaseDatabase.reference.child("ListOfDevice")
                                        .child("Floor_$i").child(t.toString()).child(address)
                                        .child("rule").child("timeOn").setValue(timeOn)

                                    firebaseDatabase.reference.child("ListOfDevice")
                                        .child("Floor_$i").child(t.toString()).child(address)
                                        .child("rule").child("timOff").setValue(timOff)
                                    firebaseDatabase.reference.child("ListOfDevice")
                                        .child("Floor_$i").child(t.toString()).child(address)
                                        .child("rule").child("onOfTemperature")
                                        .setValue(onOfTemperature)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })


                }
                //Toast.makeText(this, "$countFloor---$countRoom", Toast.LENGTH_LONG).show()
            } else {
                if (inputRuleMode == "For This Device Only") {
                    val currentFloor = getInfoAboutRoomAndFloor(3)
                    val currentRoom = getInfoAboutRoomAndFloor(4)
                    firebaseDatabase.reference.child("ListOfDevice").child("Floor_$currentFloor")
                        .child(currentRoom.toString()).child(address).child("rule")
                        .child("inputRuleType").setValue(inputRuleType)

                    firebaseDatabase.reference.child("ListOfDevice").child("Floor_$currentFloor")

                        .child(currentRoom.toString()).child(address).child("rule")
                        .child("inputPowerModeManual").setValue(inputPowerMode)

                    firebaseDatabase.reference.child("ListOfDevice").child("Floor_$currentFloor")

                        .child(currentRoom.toString()).child(address).child("rule").child("timeOn")
                        .setValue(timeOn)

                    firebaseDatabase.reference.child("ListOfDevice").child("Floor_$currentFloor")

                        .child(currentRoom.toString()).child(address).child("rule").child("timOff")
                        .setValue(timOff)
                    firebaseDatabase.reference.child("ListOfDevice").child("Floor_$currentFloor")
                        .child(currentRoom.toString()).child(address).child("rule")
                        .child("onOfTemperature").setValue(onOfTemperature)
                    // Toast.makeText(this, "$currentFloor---$currentRoom", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.costum_menu2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val projectorMode: LinearLayout = findViewById(R.id.projectorMode)
        val conditionerMOde: LinearLayout = findViewById(R.id.conditionerMode)
        return when (item.itemId) {
            R.id.projector -> {
                projectorMode.visibility = View.VISIBLE
                conditionerMOde.visibility = View.GONE
                return true
            }
            R.id.conditioner -> {
                conditionerMOde.visibility = View.VISIBLE
                projectorMode.visibility = View.GONE
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkConditionOfDevice(address: String) {
        val floor = getInfoAboutRoomAndFloor(3)
        val room = getInfoAboutRoomAndFloor(4)
        val vibrator: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        var check = true
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        firebaseDatabase.reference.child("ListOfDevice").child("Floor_$floor").child("$room")
            .child(address).child("rule").child("inputPowerModeManual")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    check = if (snapshot.value == "ON" && check) {
                        firebaseDatabase.reference.child("ListOfDevice").child("Floor_$floor")
                            .child("$room").child(address).child("rule")
                            .child("inputPowerModeManual").setValue("OFF")
                        false
                    } else {
                        if (snapshot.value == "OFF" && check) firebaseDatabase.reference.child("ListOfDevice")
                            .child("Floor_$floor").child("$room").child(address).child("rule")
                            .child("inputPowerModeManual").setValue("ON")
                        false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun getTempAndHum() {
        val temp: TextView = findViewById(R.id.temperature)
        val hum: TextView = findViewById(R.id.humidity)

        val floor = getInfoAboutRoomAndFloor(3)
        val room = getInfoAboutRoomAndFloor(4)
        val vibrator: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        firebaseDatabase.reference.child("ListOfDevice").child("Floor_$floor").child("$room")
            .child("Conditioner").child("rule").child("result")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val values = arrayOf("", "")
                    for ((i, dsnapshot) in snapshot.children.withIndex()) {
                        values[i] = "${dsnapshot.value}"
                    }
                    hum.text = values[0]
                    temp.text = values[1]
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


    }


    private fun getInfoAboutRoomAndFloor(mod: Int): Int {
        val sharedPreferences = getSharedPreferences("sharePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        var countedNumber = 0
        when (mod) {

            2 -> {
                countedNumber = sharedPreferences.getInt("numberOfFloor", 0)
            }
            3 -> {
                countedNumber = sharedPreferences.getInt("currentFloor", 0)
            }
            4 -> {
                try {
                    countedNumber = intent.getStringExtra("currentRoom").toString().toInt()
                    editor.apply {
                        putInt("currentRoom", countedNumber)
                    }.apply()
                } catch (e: Exception) {
                    countedNumber = sharedPreferences.getInt("currentRoom", 0)
                }

            }
        }

        return countedNumber
    }
}