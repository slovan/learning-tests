package mockito.helloworld.example;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static mockito.helloworld.example.IGreeting.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Source: https://examples.javacodegeeks.com/core-java/mockito/mockito-hello-world-example/
 */
public class MockitoHelloWorldExample {
    private IGreeting greeting;
    private final static ValidQuestions VALID_QUESTIONS = new ValidQuestions();

    @Before
    public void setupMock() {
        greeting = mock(IGreeting.class);
        when(greeting.greet()).thenReturn(HELLO_WORLD);
    }

    @Test
    public void testGreetings() {
        System.out.println("Greetings: " + greeting.greet());
        assertThat(greeting.greet()).isEqualTo(HELLO_WORLD);
    }

    @Test
    public void testSpeaker() {
        Speaker speaker = new Speaker();
        assertThat(HELLO_WORLD).isEqualTo(speaker.speak(greeting));
    }

    @Test(expected=GreetingNotAvailable.class)
    public void testGreetingNotAvailable() {
        Speaker speaker = new Speaker();
        System.out.println("Greeting is down so will not respond");
        when(greeting.greet()).thenReturn(null);
        System.out.println("Speaker sends a question to Greeting but since Greeting is not available will throw GreetingNotAvailable");
        speaker.question(greeting, "Hello John");
    }

    @Test
    public void testSpeakerQuestionsGreeting() {
        Speaker speaker = new Speaker();
        System.out.println("Speaker asks IGreeting 'Any new topics?', it should get a response");
        speaker.question(greeting, IGreeting.ANY_NEW_TOPICS);
        System.out.println("Verify that IGreeting has been asked the question");
        verify(greeting, times(1)).question(IGreeting.ANY_NEW_TOPICS);
    }

    @Test
    public void testFilterInvalidQuestions() {
        Speaker speaker = new Speaker();
        String invalidQuestion = "Invalid question";
        speaker.question(greeting, invalidQuestion);
        System.out.println("Verify that question was never requested as IGreeting is un-available");
        verify(greeting, never()).question(invalidQuestion);
    }

    @Test(expected = InvalidQuestion.class)
    public void testThrowExceptionIfInvalidQuestion() throws InvalidQuestion {
        Speaker speaker = new Speaker();
        String invalidQuestion = "Invalid question";
        when(greeting.questionStrictly("Invalid question")).thenThrow(new InvalidQuestion());
        speaker.questionStrictly(greeting, invalidQuestion);
    }

    @Test(expected = InvalidQuestion.class)
    public void testThrowExceptionIfAnyInvalidQuestion() throws InvalidQuestion {
        Speaker speaker = new Speaker();
        String invalidQuestion = "Invalid question";
        when(greeting.questionStrictly(argThat(new InValidQuestions()))).thenThrow(new InvalidQuestion());
        speaker.questionStrictly(greeting, invalidQuestion);
    }

    @Test
    public void testGetTodaysTopicPrice() throws InvalidQuestion {
        Speaker speaker = new Speaker();
        when(greeting.questionStrictly(argThat(new ValidQuestions()))).thenAnswer(new GreetingAnswers());
        when(greeting.getPrice(TOPIC_MOCKITO)).thenReturn(20);

        String answer = speaker.questionStrictly(greeting, ANY_NEW_TOPICS);
        System.out.println("Answer is: " + answer);
        assertThat(answer).isEqualTo("Topic is Mockito, price is 20");
        verify(greeting, times(1)).questionStrictly(WHAT_IS_TODAYS_TOPIC);
        verify(greeting, times(1)).getPrice(TOPIC_MOCKITO);
        verify(greeting, times(1)).bye();
    }

    @Test
    public void testNoNewTopic() throws InvalidQuestion {
        Speaker speaker = new Speaker();
        when(greeting.questionStrictly(ANY_NEW_TOPICS)).thenReturn(NO_NEW_TOPIC);

        String answer = speaker.questionStrictly(greeting, ANY_NEW_TOPICS);
        System.out.println("Answer is: " + answer);
        assertThat(answer).isEqualTo(NO_NEW_TOPIC);
        verify(greeting, times(1)).bye();
        verify(greeting, never()).questionStrictly(WHAT_IS_TODAYS_TOPIC);
        verify(greeting, never()).getPrice(TOPIC_MOCKITO);
    }

    private static class InValidQuestions implements ArgumentMatcher<String> {

        @Override
        public boolean matches(String argument) {
            return !VALID_QUESTIONS.matches(argument);
        }
    }

    private static class ValidQuestions implements ArgumentMatcher<String>  {

        @Override
        public boolean matches(String argument) {
            return argument.equals(ANY_NEW_TOPICS)
                    || argument.equals(WHAT_IS_TODAYS_TOPIC);
        }
    }

    private static class GreetingAnswers implements Answer {

        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
            String arg = (String) invocation.getArguments()[0];
            if (ANY_NEW_TOPICS.equals(arg)) {
                return YES_NEW_TOPICS_AVAILABLE;
            } else if (WHAT_IS_TODAYS_TOPIC.equals(arg)) {
                return TOPIC_MOCKITO;
            } else {
                throw new InvalidQuestion();
            }
        }
    }
}
