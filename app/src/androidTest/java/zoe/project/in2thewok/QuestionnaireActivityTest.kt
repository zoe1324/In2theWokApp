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
class QuestionnaireActivityTest{
    @get:Rule var activityScenarioRule = activityScenarioRule<QuestionnaireActivity>()
    @Test
    fun checkActivityVisibility(){
        onView(withId(R.id.questionnaireActivity))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingBackgroundVisibility(){
        onView(withId(R.id.background_photo))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingButtonVisibility(){
        onView(withId(R.id.btnSubmit))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingEditTextVisibility(){
        onView(withId(R.id.a1))
            .check(matches(isDisplayed()))

        onView(withId(R.id.a2))
            .check(matches(isDisplayed()))

        onView(withId(R.id.a3))
            .check(matches(isDisplayed()))

        onView(withId(R.id.a4))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingTextVisibility(){
        onView(withId(R.id.q1))
            .check(matches(isDisplayed()))

        onView(withId(R.id.q2))
            .check(matches(isDisplayed()))

        onView(withId(R.id.q3))
            .check(matches(isDisplayed()))

        onView(withId(R.id.q4))
            .check(matches(isDisplayed()))
    }
    @Test
    fun testTextMatchesExpected(){
        onView(withId(R.id.q1))
            .check(matches(withText(R.string.questionOne)))

        onView(withId(R.id.q2))
            .check(matches(withText(R.string.questionTwo)))

        onView(withId(R.id.q3))
            .check(matches(withText(R.string.questionThree)))

        onView(withId(R.id.q4))
            .check(matches(withText(R.string.questionFour)))

        onView(withId(R.id.btnSubmit))
            .check(matches(withText(R.string.submit)))
    }
}