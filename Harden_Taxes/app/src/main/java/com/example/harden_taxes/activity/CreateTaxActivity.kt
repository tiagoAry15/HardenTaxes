package com.example.harden_taxes.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.harden_taxes.Adapter.ApartmentAdapter
import com.example.harden_taxes.R
import com.example.harden_taxes.model.Apartment
import com.example.harden_taxes.model.Condominium
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class CreateTaxActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mValue: EditText
    private lateinit var mDate: EditText
    private lateinit var mSave: Button
    private var apartment = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tax)
        mDatabase = FirebaseDatabase.getInstance()
        mValue = findViewById(R.id.tax_editext_value)
        mDate = findViewById(R.id.tax_editext_date)
        mSave = findViewById(R.id.tax_button_save)
        mSave.setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()
        apartment = intent.getIntExtra("apart",1)
        mValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            var current = ""
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s != current){
                    mValue.removeTextChangedListener(this)

                    var cleanString = s.toString().replace("""[$,.]""".toRegex(), "")
                    var parsed = cleanString.toDouble()
                    var formatted = NumberFormat.getCurrencyInstance().format((parsed/100))

                    current = formatted

                    mValue.setText(formatted)
                    mValue.setSelection(formatted.length)
                    mValue.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tax_button_save ->{
              var isFormFilled = true
              val valor = mValue.text.toString()
              val data = mDate.text.toString()
              val valorDouble =  valor.replace("""[$,.]""".toRegex(), "").toDouble()/100

              if (valor.isEmpty()){
                  mValue.error  = "este campo não pode estar vazio"
                  isFormFilled = false
              }
                if (data.isEmpty()){
                    mDate.error  = "este campo não pode estar vazio"
                    isFormFilled = false
                }
                if(isFormFilled){
                     mDatabase.getReference("/users/${mAuth.uid}/Condominio/tax").setValue(valorDouble)
                    val query = mDatabase.reference.child("/users/${mAuth.uid}/Condominio/andares").orderByKey()
                    query.addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach{
                                val expense = it.getValue(Apartment::class.java)
                                val valorFinal =  valorDouble/apartment
                                  mDatabase.getReference("/users/${mAuth.uid}/Condominio/andares/${expense!!.id}/monthTax").setValue(String.format("%.2f", valorFinal))
                                mDatabase.getReference("/users/${mAuth.uid}/Condominio/andares/${expense!!.id}/vencimento").setValue(data)
                                mDatabase.getReference("/users/${mAuth.uid}/Condominio/vencimento").setValue(data)
                            }


                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                    finish()
                }


            }


        }
    }
}