package arcpotcalc;

public class APCLogic {
    public static String APCLogicMain(double bp, double score, double noteCount, boolean isCleared) {
        if (score < 9800000) {
            double scoreptt;
            if (bp > ((9500000 - score)/300000)) {
                scoreptt = bp + ((score - 9500000)/300000);
            }
            else {
                scoreptt = 0.0;
            }
            if (score >= 9500000 && score < 9800000) {
                if (isCleared) {
                    return String.format("AA | %.6f (%.3f)", scoreptt + 0.2, scoreptt + 0.2);
                }
                else {
                    return String.format("AA | %.6f (%.3f)", scoreptt, scoreptt);
                }
            }
            else if (score >= 9200000 && score < 9500000) {
                if (isCleared) {
                    return String.format("A | %.6f (%.3f)", scoreptt + 0.2, scoreptt + 0.2);
                }
                else {
                    return String.format("A | %.6f (%.3f)", scoreptt, scoreptt);
                }
            }
            else if (score >= 8900000 && score < 9200000) {
                if (isCleared) {
                    return String.format("B | %.6f (%.3f)", scoreptt + 0.2, scoreptt + 0.2);
                }
                else {
                    return String.format("B | %.6f (%.3f)", scoreptt, scoreptt);
                }
            }
            else if (score >= 8600000 && score < 8900000) {
                if (isCleared) {
                    return String.format("C | %.6f (%.3f)", scoreptt + 0.2, scoreptt + 0.2);
                }
                else {
                    return String.format("C | %.6f (%.3f)", scoreptt, scoreptt);
                }
            }
            else if (score >= 0 && score < 8600000) {
                if (isCleared) {
                    return String.format("D | %.6f (%.3f)", scoreptt + 0.2, scoreptt + 0.2);
                }
                else {
                    return String.format("D | %.6f (%.3f)", scoreptt, scoreptt);
                }
            }
        }
        else if (score >= 9800000 && score < 10000000) {
            double scoreptt = bp + 1 + ((score - 9800000)/200000);
            if (score >= 9800000 && score < 9900000) {
                if (isCleared) {
                    return String.format("EX | %.6f (%.3f)", scoreptt + 0.2, scoreptt + 0.2);
                }
                else {
                    return String.format("EX | %.6f (%.3f)", scoreptt, scoreptt);
                }
            }
            else if (score >= 9900000 && score < 10000000) {
                if (isCleared) {
                    return String.format("EX+ | %.6f (%.3f)", scoreptt + 0.2, scoreptt + 0.2);
                }
                else {
                    return String.format("EX+ | %.6f (%.3f)", scoreptt, scoreptt);
                }
            }
        }
        else if (score >= 10000000) {
            double maxScore = 10000000 + noteCount;
            if (noteCount <= 0) {
                if (isCleared) {
                    return String.format("Pure Memory | %.3f", bp + 2.2);
                }
                else {
                    return String.format("Pure Memory | %.3f", bp + 2);
                }
            }
            else if (score == maxScore) {
                if (isCleared) {
                    return String.format("Pure Memory (max) | %.3f", bp + 2.2, bp + 2.2);
                }
                else {
                    return String.format("Pure Memory (max) | %.3f", bp + 2, bp + 2);
                }
            }
            else if (score < maxScore) {
                if (isCleared) {
                    return String.format("Pure Memory (max - %.0f) | %.3f", maxScore - score, bp + 2.2);
                }
                else {
                    return String.format("Pure Memory (max - %.0f) | %.3f", maxScore - score, bp + 2);
                }
            }
            else if (score > maxScore) {
                if (isCleared) {
                    return String.format("Pure Memory (max + %.0f) | %.3f", score - maxScore, bp + 2.2);
                }
                else {
                    return String.format("Pure Memory (max + %.0f) | %.3f", score - maxScore, bp + 2);
                }
            }
        }
        return "유효하지 않은 입력이 존재합니다.";
    }
}