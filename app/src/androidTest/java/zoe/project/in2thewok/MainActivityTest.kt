package zoe.project.in2thewok

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest{
    @get:Rule var activityScenarioRule = activityScenarioRule<MainActivity>()
    @Test
    fun checkActivityVisibility(){
        onView(withId(R.id.welcomeActivity))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingBackgroundVisibility(){
        onView(withId(R.id.background_photo))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingTextVisibility(){
        onView(withId(R.id.in2thewok))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvImageCreditMain))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingButtonVisibility(){
        onView(withId(R.id.btnLoginMain))
            .check(matches(isDisplayed()))

        onView(withId(R.id.btnRegisterMain))
            .check(matches(isDisplayed()))
    }
    @Test
    fun testTextMatchesExpected(){
        onView(withId(R.id.in2thewok))
            .check(matches(withText(R.string.appName)))

        onView(withId(R.id.tvImageCreditMain))
            .check(matches(withText(R.string.backgroundCredit)))

        onView(withId(R.id.btnLoginMain))
            .check(matches(withText(R.string.login)))

        onView(withId(R.id.btnRegisterMain))
            .check(matches(withText(R.string.register)))
    }
    @Test
    fun navigateToLogin(){
        onView(withId(R.id.btnLoginMain))
            .perform(click())

        onView(withId(R.id.loginActivity))
            .check(matches(isDisplayed()))

    }
    @Test
    fun navigateToRegister(){
        onView(withId(R.id.btnRegisterMain))
            .perform(click())

        onView(withId(R.id.registerActivity))
            .check(matches(isDisplayed()))
    }
}
