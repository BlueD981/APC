package arcpotcalc;

public class APCLogic {
    public static String APCLogicMain(double bp, double score, double noteCount) {
        if (score < 9800000) {
            double scoreptt = bp + ((score - 9500000)/300000);
            if (score >= 9500000 && score < 9800000) {
                return String.format("결과 포텐셜 : AA | %.6f (%.2f)", scoreptt, scoreptt);
            }
            else if (score >= 9200000 && score < 9500000) {
                return String.format("결과 포텐셜 : A | %.6f (%.2f)", scoreptt, scoreptt);
            }
            else if (score >= 8900000 && score < 9200000) {
                return String.format("결과 포텐셜 : B | %.6f (%.2f)", scoreptt, scoreptt);
            }
            else if (score >= 8600000 && score < 8900000) {
                return String.format("결과 포텐셜 : C | %.6f (%.2f)", scoreptt, scoreptt);
            }
            else if (score >= 0 && score < 8600000) {
                return String.format("결과 포텐셜 : D | %.6f (%.2f)", scoreptt, scoreptt);
            }
        }
        else if (score >= 9800000 && score < 10000000) {
            double scoreptt = bp + 1 + ((score - 9800000)/200000);
            if (score >= 9800000 && score < 9900000) {
                return String.format("결과 포텐셜 : EX | %.6f (%.2f)", scoreptt, scoreptt);
            }
            else if (score >= 9900000 && score < 10000000) {
                return String.format("결과 포텐셜 : EX+ | %.6f (%.2f)", scoreptt, scoreptt);
            }
        }
        else if (score >= 10000000) {
            double maxScore = 10000000 + noteCount;
            if (noteCount <= 0) {
                return String.format("결과 포텐셜 : Pure Memory | %.2f", bp + 2);
            }
            else if (score == maxScore) {
                return String.format("결과 포텐셜 : Pure Memory (max) | %.2f", bp + 2);
            }
            else if (score < maxScore) {
                return String.format("결과 포텐셜 : Pure Memory (max - %.0f) | %.2f", maxScore - score, bp + 2);
            }
            else if (score > maxScore) {
                return String.format("결과 포텐셜 : Pure Memory (max + %.0f) | %.2f", score - maxScore, bp + 2);
            }
        }
        return "유효하지 않은 입력이 존재합니다.";
    }
}