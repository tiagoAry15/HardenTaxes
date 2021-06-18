package com.example.harden_taxes.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.harden_taxes.R
import com.example.harden_taxes.model.Apartment
import com.example.harden_taxes.model.Condominium
import com.example.harden_taxes.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.floor

class RegistApartmentActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mName: EditText
    private lateinit var mAdress: EditText
    private lateinit var mNumber: EditText
    private lateinit var mFloor: EditText
    private lateinit var mApFloor: EditText
    private lateinit var mSindiFloor: EditText
    private lateinit var mRegister: Button
    private lateinit var mTax: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regist_apartment)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mName =findViewById(R.id.apartfrom_editext_name)
        mAdress = findViewById(R.id.apartfrom_editext_adress)
        mNumber = findViewById(R.id.apartfrom_editext_adnumber)
        mFloor = findViewById(R.id.apartfrom_editext_floor)
        mApFloor = findViewById(R.id.apartfrom_editext_floorQuant)
        mRegister = findViewById(R.id.apartfrom_button_register)
        mSindiFloor = findViewById(R.id.apartfrom_editext_SindiFloor)

        mRegister.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.apartfrom_button_register -> {
                val name = mName.text.toString()
                val adress = mAdress.text.toString()
                val adnumber = mNumber.text.toString()
                val floor = mFloor.text.toString()
                val floorquant = mApFloor.text.toString()
                val sindifloor = mSindiFloor.text.toString()
                var isFormFilled = true
                if(name.isEmpty()){
                    mName.error  = "este campo não pode estar vazio"
                    isFormFilled = false
                }
                if(adress.isEmpty()){
                    mAdress.error  = "este campo não pode estar vazio"
                    isFormFilled = false
                }
                if(adnumber.isEmpty()){
                    mNumber.error  = "este campo não pode estar vazio"
                    isFormFilled = false
                }
                if(floor.isEmpty()){
                    mFloor.error  = "este campo não pode estar vazio"
                    isFormFilled = false
                }
                if(floorquant.isEmpty()){
                    mApFloor.error  = "este campo não pode estar vazio"
                    isFormFilled = false
                }

                if(isFormFilled){
                    val ref = mDatabase.getReference("/users/${mAuth.uid}/Condominio")
                    val Condominium = Condominium(name,adress,adnumber.toInt(),(floor.toInt() * floorquant.toInt()),0.0)
                    ref.setValue(Condominium)
                    for(i in 1..floor.toInt()){
                        for(j in 1..floorquant.toInt()){
                            val transactionId = mDatabase.reference.child("/users/${mAuth.uid}/Condominio/andares").push().key
                            val ref2 = mDatabase.getReference("/users/${mAuth.uid}/Condominio/andares/${transactionId}")
                            var regularidade:Boolean = j% 2 != 0
                            if((i * 100 + j).toString() == sindifloor){
                             val userQuery = mDatabase.reference.child("/users/${mAuth.uid}").orderByKey()
                                userQuery.addValueEventListener(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val sindico = snapshot.getValue(User::class.java)
                                        val inquilino = Apartment(transactionId!!,
                                            sindico!!.firstName,
                                            sindico.lastName,
                                            sindico.email,
                                            sindico.telefone,
                                            (i * 100 + j),
                                        regular = false)
                                        ref2.setValue(inquilino)
                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                })

                                }
                            else{
                                val inquilino = Apartment(id = transactionId!!, floor = (i * 100 + j), regular = regularidade)
                                ref2.setValue(inquilino)
                            }

                            Toast.makeText(
                                applicationContext, "Condomínio criado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                }


            }
        }
    }
}