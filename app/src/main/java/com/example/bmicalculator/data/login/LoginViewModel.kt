package com.example.bmicalculator.data.login


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.bmicalculator.Navigation.PostOfficeAppRouter
import com.example.bmicalculator.Navigation.Screen
import com.example.bmicalculator.data.rules.Validator
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val TAG = LoginViewModel::class.simpleName

   var loginUIState = mutableStateOf(LoginUIState())
    var allValidationsPassed = mutableStateOf(false)
    var loginInProgress = mutableStateOf(false)

    fun validateLoginUIDataWithRules() {
        val emailResult = Validator.validateEmail(
            email = loginUIState.value.email
        )


        val passwordResult = Validator.validatePassword(
            password = loginUIState.value.password
        )

        loginUIState.value = loginUIState.value.copy(
            emailError = emailResult.status,
            passwordError = passwordResult.status
        )

        allValidationsPassed.value = emailResult.status && passwordResult.status  // update this mutable state

    }

    fun login() {

        loginInProgress.value = true
        val email = loginUIState.value.email
        val password = loginUIState.value.password

        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                Log.d(TAG,"Inside_login_success")
                Log.d(TAG,"${it.isSuccessful}")

                if(it.isSuccessful){
                    loginInProgress.value = false
                    PostOfficeAppRouter.navigateTo(Screen.HomeScreen)
                }
            }
            .addOnFailureListener {
                Log.d(TAG,"Inside_login_failure")
                Log.d(TAG,"${it.localizedMessage}")

                loginInProgress.value = false

            }

    }

}

fun LoginViewModel.onEvent(event: LoginUIEvent) {
    when(event) {
        is LoginUIEvent.EmailChanged -> {
            loginUIState.value = loginUIState.value.copy(email = event.email)
        }
        is LoginUIEvent.PasswordChanged -> {
            loginUIState.value = loginUIState.value.copy(password = event.password)
        }
        is LoginUIEvent.LoginButtonClicked -> {
            login()
        }
    }
    validateLoginUIDataWithRules()

}
