package QuizzMVC.Quizz.controller;

import QuizzMVC.Quizz.service.QuizServiceException;
import org.springframework.ui.Model;
import QuizzMVC.Quizz.model.Question;
import QuizzMVC.Quizz.service.QuizService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;


import java.util.ArrayList;
import java.util.List;


@Controller
@SessionAttributes({"pseudo", "index", "listeQuestions"})
public class QuizzController {


    @Autowired // Spring will automatically inject the QuizService instance
    private QuizService quizService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }


    @PostMapping("/start")
    public String startQuiz(@RequestParam("pseudo") String pseudo, HttpSession session) {
        session.setAttribute("pseudo", pseudo);
        session.setAttribute("index", 0); // Initial index for the first question
        return "redirect:/quizz"; // Redirect to the quizz page
    }


    @GetMapping("/quizz")
    public String showQuizz(HttpSession session, Model model) throws QuizServiceException {
        String pseudo = (String) session.getAttribute("pseudo");
        int index = (int) session.getAttribute("index");
        List<Question> questions = quizService.loadQuestions();

        if (index >= questions.size()) {
            // All questions have been answered, redirect to the result page
            return "redirect:/results";
        }

        model.addAttribute("pseudo", pseudo);
        model.addAttribute("question", questions.get(index));
        model.addAttribute("index", index + 1); // Question number starts from 1
        model.addAttribute("listeQuestions", questions); // Add the list of questions to the model

        return "quizz";
    }


    @PostMapping("/submitAnswer")
    public String submitAnswer(HttpSession session, @RequestParam("answer") int selectedAnswerIndex) throws QuizServiceException {
        int index = (int) session.getAttribute("index");
        List<Question> questions = quizService.loadQuestions();
        Integer correctAnswers = (Integer) session.getAttribute("correctAnswers");
        if (correctAnswers == null) {
            correctAnswers = 0;
        }

        // Vérifiez si l'index de la question est valide
        if (index < 0 || index >= questions.size()) {
            throw new QuizServiceException("Current question index is out of bounds");
        }

        int score = quizService.validateAnswer(selectedAnswerIndex, index, questions);
        // Mettre à jour le nombre de réponses correctes dans la session
        session.setAttribute("correctAnswers", correctAnswers + score);

        // Incrémentez l'index de la question pour passer à la suivante
        session.setAttribute("index", index + 1);

        // Redirigez vers la page des résultats si toutes les questions ont été répondues
        if (index + 1 >= questions.size()) {
            return "redirect:/results";
        } else {
            // Redirigez vers la page du quiz pour afficher la prochaine question
            return "redirect:/quizz";
        }
    }



    @GetMapping("/results")
    public String showResult(HttpSession session, Model model) {
        Integer correctAnswers = (Integer) session.getAttribute("correctAnswers");
        List<Question> questions = (List<Question>) session.getAttribute("listeQuestions");

        if (correctAnswers == null || questions == null || questions.isEmpty()) {
            // Gérer le cas où le nombre de réponses correctes ou la liste de questions n'est pas disponible dans la session
            // Redirigez l'utilisateur vers une page d'erreur ou une autre page appropriée
            return "redirect:/error";
        }

        int score = getScore(questions, correctAnswers);
        model.addAttribute("score", score); // Ajoutez la variable score au modèle
        return "results";
    }

    // Méthode pour calculer le score
    public int getScore(List<Question> questions, int correctAnswers) {
        int totalQuestions = questions.size();
        return (correctAnswers * 100) / totalQuestions; // Calcule le score en pourcentage
    }
}

