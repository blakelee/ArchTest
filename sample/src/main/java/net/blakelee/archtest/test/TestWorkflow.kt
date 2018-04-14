package net.blakelee.archtest.test

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import net.blakelee.archtest.test.first.FirstScreen
import net.blakelee.archtest.test.second.SecondScreen
import net.blakelee.library.AbstractViewFactory
import net.blakelee.library.FiniteStateMachine
import net.blakelee.library.FiniteStateMachine.Companion.onEntry
import net.blakelee.library.FiniteStateMachine.Companion.transition
import net.blakelee.library.Workflow
import net.blakelee.library.WorkflowScreen

internal enum class State {
    FIRST_SCREEN, SECOND_SCREEN
}

class TestWorkflow : Workflow<Unit, Unit>,
    FirstScreen.Events, SecondScreen.Events {

    override var viewFactory: AbstractViewFactory = TestViewFactory()
    override var currentScreen: BehaviorSubject<String> = BehaviorSubject.create<String>()

    private val firstMessage = BehaviorSubject.create<String>()
    private val secondMessage = BehaviorSubject.create<String>()

    override var stateMachine = FiniteStateMachine(
            State.FIRST_SCREEN,

            transition(State.FIRST_SCREEN, Integer::class, State.SECOND_SCREEN) ,
            transition(State.SECOND_SCREEN, Integer::class, State.FIRST_SCREEN),
            onEntry(State.FIRST_SCREEN) { currentScreen.onNext(FirstScreen.KEY) },
            onEntry(State.SECOND_SCREEN) { currentScreen.onNext(SecondScreen.KEY) }
    )

    override fun screen(): Observable<WorkflowScreen<*, *>> =
            currentScreen.map {
                when(it) {
                    FirstScreen.KEY -> FirstScreen(firstMessage, this)
                    SecondScreen.KEY -> SecondScreen(secondMessage, this)
                    else -> throw IllegalArgumentException("Unknown key $it")
                }
            }

    override fun firstEvents() {
        event(1)
    }

    override fun secondTest() {
        event(2)
    }
}