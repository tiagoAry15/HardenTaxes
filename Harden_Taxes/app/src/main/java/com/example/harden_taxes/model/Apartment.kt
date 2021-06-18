package com.example.harden_taxes.model

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class Apartment (
    val id:String = "",
    val firstName:String? = "Sem Inquilino",
    val lastName:String? = "",
    val email:String?= "",
    val telefone:String? = "",
    val floor:Int = 0,
    val regular:Boolean = true,
    val monthTax: String = "0",
    val vencimento:String = ""
        )
