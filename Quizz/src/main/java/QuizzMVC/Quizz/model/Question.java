package QuizzMVC.Quizz.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Question {
        private String text;
        private List<String> options;
        private String correctAnswer;
}
