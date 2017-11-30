package mockito.helloworld.example;

/**
 * Source: https://examples.javacodegeeks.com/core-java/mockito/mockito-hello-world-example/
 */
public class Speaker {
    public String speak(IGreeting greeting) {
        System.out.println("Speaker invokes greetings");
        return greeting.greet();
    }

    public String question(IGreeting greeting, String question) {
        verifyGreetingConnection(greeting);
        if (IGreeting.ANY_NEW_TOPICS.equals(question)) {
            return greeting.question(question);
        }
        return "Invalid Question";
    }

    public String questionStrictly(IGreeting greeting, String question) throws InvalidQuestion {
        verifyGreetingConnection(greeting);
        System.out.println(question);
        String answer = greeting.questionStrictly(question);
        switch (answer) {
            case IGreeting.NO_NEW_TOPIC:
                System.out.println("No");
                System.out.println("Let's quit now");
                greeting.bye();
                break;
            case IGreeting.YES_NEW_TOPICS_AVAILABLE:
                System.out.println("Yes");
                System.out.println(IGreeting.WHAT_IS_TODAYS_TOPIC);
                answer = greeting.questionStrictly(IGreeting.WHAT_IS_TODAYS_TOPIC);
                System.out.println("Topic name is " + answer);
                System.out.println("What is the price?");
                int price = greeting.getPrice(answer);
                System.out.println("Price is " + price);
                System.out.println("Let's quit now");
                greeting.bye();
                answer = "Topic is " + answer + ", price is " + price;
                break;
            default:
                System.out.println("Answer is " + answer);
                break;
        }
        return answer;
    }

    public void verifyGreetingConnection(IGreeting greeting) {
        System.out.println("Is Greeting available?");
        String response = greeting.greet();
        if (!IGreeting.HELLO_WORLD.equals(response)) {
            System.out.println("No");
            throw new GreetingNotAvailable();
        }
        System.out.println("Yes");
    }
}
