package com.agmobiletech.drivetogether

import com.google.gson.annotations.SerializedName

data class JsonUser (

    @SerializedName("email")
    var emailUser : String?,

    @SerializedName("nome")
    val nameUser : String?,

    @SerializedName("cognome")
    val surnameUser : String?,

    @SerializedName("dataNascita")
    var dateUser : String?,

    @SerializedName("citta")
    val cityUser : String?,

    @SerializedName("telefono")
    val phoneUser : String?,

    @SerializedName("cartaCredito")
    var creditUser : String?,

    @SerializedName("password")
    val passwUser : String?,

    @SerializedName("immagineProfilo")
    val proPicUser : String?
)
