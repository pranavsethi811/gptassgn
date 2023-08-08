import java.util.Scanner;

/**
 * The GradeCalculator interface defines methods for calculating average and remaining grades.
 */
interface GradeCalculator {
    /**
     * Calculates the average grade based on the given weightages and grades.
     *
     * @param weightages The weightages of each assignment/quiz.
     * @param grades     The grades obtained in each assignment/quiz.
     * @return The calculated average grade.
     */
    double calculateAverageGrade(double[] weightages, double[] grades);
    /**
     * Calculates the average grade needed on the remaining assignments/quizzes
     * to achieve the desired overall average.
     *
     * @param grades          The grades obtained in completed assignments/quizzes.
     * @param weightages      The weightages of completed assignments/quizzes.
     * @param desiredAverage  The desired overall average grade.
     * @param remainingWeightage The weightage of remaining assignments/quizzes.
     * @return The average grade needed on the remaining assignments/quizzes.
     */
    double calculateRemainingGrade(double[] grades, double[] weightages, double desiredAverage, double remainingWeightage);
}

class SimpleGradeCalculator implements GradeCalculator {
    @Override
    public double calculateAverageGrade(double[] weightages, double[] grades) {
        double totalWeightedGrade = 0;
        double totalWeightage = 0;

        for (int i = 0; i < weightages.length; i++) {
            totalWeightedGrade += weightages[i] * grades[i];
            totalWeightage += weightages[i];
        }

        return totalWeightedGrade / totalWeightage;
    }

    @Override
    public double calculateRemainingGrade(double[] grades, double[] weightages, double desiredAverage, double remainingWeightage) {
        double totalWeightedGrade = 0;
        double totalWeightage = 0;

        for (int i = 0; i < weightages.length; i++) {
            totalWeightedGrade += weightages[i] * grades[i];
            totalWeightage += weightages[i];
        }

        double currentAverage = totalWeightedGrade / totalWeightage;
        double currentWeightage = totalWeightage + remainingWeightage;
        double requiredTotalGrade = desiredAverage * currentWeightage;
        double remainingTotalGrade = requiredTotalGrade - totalWeightedGrade;

        if (remainingTotalGrade <= 0) {
            return 0;
        }

        return remainingTotalGrade / remainingWeightage;
    }
}

class CGPAGradeCalculator implements GradeCalculator {
    @Override
    public double calculateAverageGrade(double[] weightages, double[] grades) {
        double totalWeightedGrade = calculateWeightedSum(weightages, grades);
        double totalWeightage = sumArray(weightages);

        return totalWeightedGrade / totalWeightage;
    }

    @Override
    public double calculateRemainingGrade(double[] grades, double[] weightages, double desiredAverage, double remainingWeightage) {
        double currentAverage = calculateAverageGrade(weightages, grades);
        double currentWeightage = sumArray(weightages) + remainingWeightage;
        double requiredTotalGrade = desiredAverage * currentWeightage;
        double currentTotalGrade = calculateWeightedSum(weightages, grades);
        double remainingTotalGrade = requiredTotalGrade - currentTotalGrade;

        if (remainingTotalGrade <= 0) {
            return 0;
        }

        return remainingTotalGrade / remainingWeightage;
    }

    private double calculateWeightedSum(double[] weightages, double[] grades) {
        double weightedSum = 0;

        for (int i = 0; i < weightages.length; i++) {
            weightedSum += weightages[i] * grades[i];
        }

        return weightedSum;
    }

    private double sumArray(double[] array) {
        double sum = 0;

        for (double value : array) {
            sum += value;
        }

        return sum;
    }
}

/**
 * Application Layer
 * Provides a higher-level interface to interact with grade calculations.
 */
interface AverageGradeCalculatorInterface {
    /**
     * Orchestrates the calculation process to calculate the average grade and the average grade in CGPA and letter grade forms.
     * Also, calculates the average grade needed on the remaining assignments/quizzes (if applicable).
     */
    void calculateAverageAndRemainingGrade();
}

class AverageGradeCalculator implements AverageGradeCalculatorInterface {
    private final GradeCalculator gradeCalculator;

    public AverageGradeCalculator(GradeCalculator gradeCalculator) {
        this.gradeCalculator = gradeCalculator;
    }

    @Override
    public void calculateAverageAndRemainingGrade() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Average Grade Calculator!");

        System.out.print("Enter the number of assignments/quizzes: ");
        int numItems = getValidNumberOfItems(scanner);

        double[] weightages = new double[numItems];
        double[] grades = new double[numItems];

        for (int i = 0; i < numItems; i++) {
            weightages[i] = getValidInput(scanner, "Enter the weightage for item " + (i + 1) + " (in percentage, between 0 and 100 inclusive): ", 0, 100);
            grades[i] = getValidInput(scanner, "Enter the grade for item " + (i + 1) + " (out of 100, between 0 and 100 inclusive): ", 0, 100);
        }

        double averageGrade = gradeCalculator.calculateAverageGrade(weightages, grades);
        System.out.println("Your current average grade is: " + averageGrade);

        double cgpa = calculateCGPA(averageGrade);
        System.out.println("Your current average grade in CGPA form is: " + cgpa);

        char letterGrade = calculateLetterGrade(averageGrade);
        System.out.println("Your current average grade in letter grade form is: " + letterGrade);

        if (totalWeightage(weightages) < 100) {
            double desiredAverage = getValidInput(scanner, "Enter your desired overall average grade (out of 100, between 0 and 100 inclusive): ", 0, 100);
            double remainingWeightage = 100 - totalWeightage(weightages);
            double remainingGrade = gradeCalculator.calculateRemainingGrade(grades, weightages, desiredAverage, remainingWeightage);

            if (remainingGrade < 0 || remainingGrade > 100) {
                System.out.println("It's not possible to achieve the desired overall average with the remaining assignments/quizzes.");
            } else {
                System.out.println("You need to get an average of " + remainingGrade + " on the remaining assignments/quizzes to achieve your desired overall average.");
            }
        }

        scanner.close();
    }

    private int getValidNumberOfItems(Scanner scanner) {
        int numItems;
        do {
            System.out.print("Enter the number of assignments/quizzes: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a positive integer.");
                scanner.next(); // Clear the invalid input
            }
            numItems = scanner.nextInt();
            if (numItems <= 0) {
                System.out.println("Invalid input! Please enter a positive integer.");
            }
        } while (numItems <= 0);
        return numItems;
    }

    private double totalWeightage(double[] weightages) {
        double totalWeightage = 0;
        for (double weightage : weightages) {
            totalWeightage += weightage;
        }
        return totalWeightage;
    }

    private double getValidInput(Scanner scanner, String prompt, double min, double max) {
        double input;
        do {
            System.out.print(prompt);
            while (!scanner.hasNextDouble()) {
                System.out.println("Invalid input! Please enter a numeric value.");
                scanner.next(); // Discard the non-numeric input
            }
            input = scanner.nextDouble();
            if (input < min || input > max) {
                System.out.println("Invalid input! Please enter a value between " + min + " and " + max + ".");
            }
        } while (input < min || input > max);
        return input;
    }

    private double calculateCGPA(double averageGrade) {
        if (averageGrade >= 90) {
            return 4.0;
        } else if (averageGrade >= 80) {
            return 3.0;
        } else if (averageGrade >= 70) {
            return 2.0;
        } else if (averageGrade >= 60) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    private char calculateLetterGrade(double averageGrade) {
        if (averageGrade >= 90) {
            return 'A';
        } else if (averageGrade >= 80) {
            return 'B';
        } else if (averageGrade >= 70) {
            return 'C';
        } else if (averageGrade >= 60) {
            return 'D';
        } else {
            return 'F';
        }
    }
}

// Main Driver
public class Main {
    public static void main(String[] args) {
        GradeCalculator gradeCalculator = new CGPAGradeCalculator();
        AverageGradeCalculatorInterface averageGradeCalculator = new AverageGradeCalculator(gradeCalculator);
        averageGradeCalculator.calculateAverageAndRemainingGrade();
    }
}
