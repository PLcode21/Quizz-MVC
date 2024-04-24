package QuizzMVC.Quizz.service;

import QuizzMVC.Quizz.model.Question;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Service
public class QuizService {

    @Value("${quiz.questions.file}") // j'ai choisi la méthode qui consiste à définir la source dans application.properties
    private String filePath;

    public List<Question> loadQuestions() throws QuizServiceException {
        Path path = Paths.get(filePath);
        try {
            String content = Files.readString(path);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, new TypeReference<List<Question>>() {});
        } catch (IOException e) {
            throw new QuizServiceException("Error reading questions file: " + e.getMessage());
        }
    }

    public int validateAnswer(int selectedAnswerIndex, int currentQuestionIndex, List<Question> questions) throws QuizServiceException {
        // Check if the current question index is valid
        if (currentQuestionIndex < 0 || currentQuestionIndex >= questions.size()) {
            throw new QuizServiceException("Current question index is out of bounds");
        }

        Question currentQuestion = questions.get(currentQuestionIndex);

        String selectedAnswer = currentQuestion.getOptions().get(selectedAnswerIndex);
        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            return 1; // Correct answer
        } else {
            return 0; // Incorrect answer
        }
    }



    // Additional methods for quiz management (optional)


    public int getScore(List<Question> questions, int correctAnswers) {
        int totalQuestions = questions.size();
        return (correctAnswers * 100) / totalQuestions; // Calculates percentage score
    }
}




