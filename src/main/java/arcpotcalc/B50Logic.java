package arcpotcalc;

import java.util.ArrayList;

public class B50Logic {
    public static final ArrayList<String> Title = new ArrayList<String>();
    public static final ArrayList<Integer> Score = new ArrayList<Integer>();
    public static final ArrayList<String> ScoreStr = new ArrayList<String>();
    public static final ArrayList<String> Artist = new ArrayList<String>();
    public static final ArrayList<String> Cleared = new ArrayList<String>();
    public static final ArrayList<Boolean> ClearedBool = new ArrayList<Boolean>();

    public static void B50LogicMain(String ArcaeaOnlineInput) {
        Title.clear();
        Score.clear();
        ScoreStr.clear();
        Artist.clear();
        Cleared.clear();
        ClearedBool.clear();

        String[] StringInput = ArcaeaOnlineInput.split("곡 ");
        for (int i = 0; i < StringInput.length - 2; i++) {
            /*System.out.println(StringInput[1 + i]);*/
            String TitleString = "";
            if (StringInput[1 + i].substring(StringInput[1 + i].indexOf("제목") + 1, StringInput[1 + i].indexOf("작곡가")).equals("목͟͝͞Ⅱ́̕")) {
                TitleString = "(II)";
            }
            else {
                TitleString = StringInput[1 + i].substring(StringInput[1 + i].indexOf("제목") + 2, StringInput[1 + i].indexOf("작곡가"));
            }
            System.out.println(TitleString);
            String ScoreRaw = StringInput[1 + i].substring(StringInput[1 + i].indexOf("작곡가"), StringInput[1 + i].indexOf("스코어"));
            System.out.println(ScoreRaw);

            if (TitleString.length() - 3 > 0 && TitleString.substring(TitleString.length() - 3, TitleString.length()).contains("INS")) {
                Title.add(TitleString.substring(0, TitleString.length() - 3));
            }
            else {
                Title.add(TitleString.substring(0, TitleString.length()));
            }

            if (ScoreRaw.contains("EX+")) {
                Score.add(Integer.parseInt((StringInput[1 + i].substring(StringInput[1 + i].indexOf("EX+") + 3, StringInput[1 + i].indexOf("스코어"))).replace(",", "").replace("P", "").replace("F", "").replace("C", "").replace("L", "")));
                String commaScore = String.format("%,d", Score.get(i));
                String commaScoreB = commaScore.replace(",", "\'");
                String zeros = "00\'000\'000";
                String ScoreWithZeros = String.format(zeros.substring(0, 10 - commaScoreB.length()) + commaScoreB);
                ScoreStr.add(ScoreWithZeros);
                Artist.add(ScoreRaw.substring(ScoreRaw.indexOf("작곡가") + 3, ScoreRaw.indexOf("EX+")));
                Cleared.add(ScoreRaw.substring(ScoreRaw.indexOf("EX+") + 13, ScoreRaw.indexOf("EX+") + 14));
                if (ScoreRaw.substring(ScoreRaw.indexOf("EX+") + 13, ScoreRaw.indexOf("EX+") + 14).equals("P") || ScoreRaw.substring(ScoreRaw.indexOf("EX+") + 13, ScoreRaw.indexOf("EX+") + 14).equals("F") || ScoreRaw.substring(ScoreRaw.indexOf("EX+") + 13, ScoreRaw.indexOf("EX+") + 14).equals("C")) {
                    ClearedBool.add(true);
                }
                else {
                    ClearedBool.add(false);
                }
            }
            else if (ScoreRaw.contains("EX0")) {
                Score.add(Integer.parseInt((StringInput[1 + i].substring(StringInput[1 + i].indexOf("EX0") + 3, StringInput[1 + i].indexOf("스코어"))).replace(",", "").replace("P", "").replace("F", "").replace("C", "").replace("L", "")));
                String commaScore = String.format("%,d", Score.get(i));
                String commaScoreB = commaScore.replace(",", "\'");
                String zeros = "00\'000\'000";
                String ScoreWithZeros = String.format(zeros.substring(0, 10 - commaScoreB.length()) + commaScoreB);
                ScoreStr.add(ScoreWithZeros);
                Artist.add(ScoreRaw.substring(ScoreRaw.indexOf("작곡가") + 3, ScoreRaw.indexOf("EX0")));
                Cleared.add(ScoreRaw.substring(ScoreRaw.indexOf("EX0") + 12, ScoreRaw.indexOf("EX0") + 13));
                if (ScoreRaw.substring(ScoreRaw.indexOf("EX0") + 12, ScoreRaw.indexOf("EX0") + 13).equals("P") || ScoreRaw.substring(ScoreRaw.indexOf("EX0") + 12, ScoreRaw.indexOf("EX0") + 13).equals("F") || ScoreRaw.substring(ScoreRaw.indexOf("EX0") + 12, ScoreRaw.indexOf("EX0") + 13).equals("C")) {
                    ClearedBool.add(true);
                }
                else {
                    ClearedBool.add(false);
                }
            }
            else if (ScoreRaw.contains("AA0")) {
                Score.add(Integer.parseInt((StringInput[1 + i].substring(StringInput[1 + i].indexOf("AA0") + 3, StringInput[1 + i].indexOf("스코어"))).replace(",", "").replace("P", "").replace("F", "").replace("C", "").replace("L", "")));
                String commaScore = String.format("%,d", Score.get(i));
                String commaScoreB = commaScore.replace(",", "\'");
                String zeros = "00\'000\'000";
                String ScoreWithZeros = String.format(zeros.substring(0, 10 - commaScoreB.length()) + commaScoreB);
                ScoreStr.add(ScoreWithZeros);
                Artist.add(ScoreRaw.substring(ScoreRaw.indexOf("작곡가") + 3, ScoreRaw.indexOf("AA0")));
                Cleared.add(ScoreRaw.substring(ScoreRaw.indexOf("AA0") + 12, ScoreRaw.indexOf("AA0") + 13));
                if (ScoreRaw.substring(ScoreRaw.indexOf("AA0") + 12, ScoreRaw.indexOf("AA0") + 13).equals("P") || ScoreRaw.substring(ScoreRaw.indexOf("AA0") + 12, ScoreRaw.indexOf("AA0") + 13).equals("F") || ScoreRaw.substring(ScoreRaw.indexOf("AA0") + 12, ScoreRaw.indexOf("AA0") + 13).equals("C")) {
                    ClearedBool.add(true);
                }
                else {
                    ClearedBool.add(false);
                }
            }
            else if (ScoreRaw.contains("A0")) {
                Score.add(Integer.parseInt((StringInput[1 + i].substring(StringInput[1 + i].indexOf("A0") + 1, StringInput[1 + i].indexOf("스코어") - 1)).replace(",", "").replace("P", "").replace("F", "").replace("C", "").replace("L", "")));
                String commaScore = String.format("%,d", Score.get(i));
                String commaScoreB = commaScore.replace(",", "\'");
                String zeros = "00\'000\'000";
                String ScoreWithZeros = String.format(zeros.substring(0, 10 - commaScoreB.length()) + commaScoreB);
                ScoreStr.add(ScoreWithZeros);
                Artist.add(ScoreRaw.substring(ScoreRaw.indexOf("작곡가") + 3, ScoreRaw.indexOf("A0")));
                Cleared.add(ScoreRaw.substring(ScoreRaw.indexOf("A0") + 11, ScoreRaw.indexOf("A0") + 12));
                if (ScoreRaw.substring(ScoreRaw.indexOf("A0") + 11, ScoreRaw.indexOf("A0") + 12).equals("P") || ScoreRaw.substring(ScoreRaw.indexOf("A0") + 11, ScoreRaw.indexOf("A0") + 12).equals("F") || ScoreRaw.substring(ScoreRaw.indexOf("A0") + 11, ScoreRaw.indexOf("A0") + 12).equals("C")) {
                    ClearedBool.add(true);
                }
                else {
                    ClearedBool.add(false);
                }
            }
            else if (ScoreRaw.contains("B0")) {
                Score.add(Integer.parseInt((StringInput[1 + i].substring(StringInput[1 + i].indexOf("B0") + 1, StringInput[1 + i].indexOf("스코어") - 1)).replace(",", "").replace("P", "").replace("F", "").replace("C", "").replace("L", "")));
                String commaScore = String.format("%,d", Score.get(i));
                String commaScoreB = commaScore.replace(",", "\'");
                String zeros = "00\'000\'000";
                String ScoreWithZeros = String.format(zeros.substring(0, 10 - commaScoreB.length()) + commaScoreB);
                ScoreStr.add(ScoreWithZeros);
                Artist.add(ScoreRaw.substring(ScoreRaw.indexOf("작곡가") + 3, ScoreRaw.indexOf("B0")));
                Cleared.add(ScoreRaw.substring(ScoreRaw.indexOf("B0") + 11, ScoreRaw.indexOf("B0") + 12));
                if (ScoreRaw.substring(ScoreRaw.indexOf("B0") + 11, ScoreRaw.indexOf("B0") + 12).equals("P") || ScoreRaw.substring(ScoreRaw.indexOf("B0") + 11, ScoreRaw.indexOf("B0") + 12).equals("F") || ScoreRaw.substring(ScoreRaw.indexOf("B0") + 11, ScoreRaw.indexOf("B0") + 12).equals("C")) {
                    ClearedBool.add(true);
                }
                else {
                    ClearedBool.add(false);
                }
            }
            else if (ScoreRaw.contains("C0")) {
                Score.add(Integer.parseInt((StringInput[1 + i].substring(StringInput[1 + i].indexOf("C0") + 1, StringInput[1 + i].indexOf("스코어") - 1)).replace(",", "").replace("P", "").replace("F", "").replace("C", "").replace("L", "")));
                String commaScore = String.format("%,d", Score.get(i));
                String commaScoreB = commaScore.replace(",", "\'");
                String zeros = "00\'000\'000";
                String ScoreWithZeros = String.format(zeros.substring(0, 10 - commaScoreB.length()) + commaScoreB);
                ScoreStr.add(ScoreWithZeros);
                Artist.add(ScoreRaw.substring(ScoreRaw.indexOf("작곡가") + 3, ScoreRaw.indexOf("C0")));
                Cleared.add(ScoreRaw.substring(ScoreRaw.indexOf("C0") + 11, ScoreRaw.indexOf("C0") + 12));
                if (ScoreRaw.substring(ScoreRaw.indexOf("C0") + 11, ScoreRaw.indexOf("C0") + 12).equals("P") || ScoreRaw.substring(ScoreRaw.indexOf("C0") + 11, ScoreRaw.indexOf("C0") + 12).equals("F") || ScoreRaw.substring(ScoreRaw.indexOf("C0") + 11, ScoreRaw.indexOf("C0") + 12).equals("C")) {
                    ClearedBool.add(true);
                }
                else {
                    ClearedBool.add(false);
                }
            }
            else if (ScoreRaw.contains("D0")) {
                Score.add(Integer.parseInt((StringInput[1 + i].substring(StringInput[1 + i].indexOf("D0") + 1, StringInput[1 + i].indexOf("스코어") - 1)).replace(",", "").replace("P", "").replace("F", "").replace("C", "").replace("L", "")));
                String commaScore = String.format("%,d", Score.get(i));
                String commaScoreB = commaScore.replace(",", "\'");
                String zeros = "00\'000\'000";
                String ScoreWithZeros = String.format(zeros.substring(0, 10 - commaScoreB.length()) + commaScoreB);
                ScoreStr.add(ScoreWithZeros);
                Artist.add(ScoreRaw.substring(ScoreRaw.indexOf("작곡가") + 3, ScoreRaw.indexOf("D0")));
                Cleared.add(ScoreRaw.substring(ScoreRaw.indexOf("D0") + 11, ScoreRaw.indexOf("D0") + 12));
                if (ScoreRaw.substring(ScoreRaw.indexOf("D0") + 11, ScoreRaw.indexOf("D0") + 12).equals("P") || ScoreRaw.substring(ScoreRaw.indexOf("D0") + 11, ScoreRaw.indexOf("D0") + 12).equals("F") || ScoreRaw.substring(ScoreRaw.indexOf("D0") + 11, ScoreRaw.indexOf("D0") + 12).equals("C")) {
                    ClearedBool.add(true);
                }
                else {
                    ClearedBool.add(false);
                }
            }
            /*System.out.println(Title.get(i));
            System.out.println(Score.get(i));*/

        }

        for (int j = Title.size(); j < 10; j++) {
            Title.add("APC_NO_TITLE");
            Score.add(-1);
            ScoreStr.add("N");
            Artist.add("APC_NO_ARTIST");
            Cleared.add("N");
        }
    }

    public ArrayList<String> getTitles() {
        return Title;
    }

    public ArrayList<Integer> getScore() {
        return Score;
    }
    
    public ArrayList<String> getScoreStr() {
        return ScoreStr;
    }

    public ArrayList<String> getArtist() {
        return Artist;
    }

    public ArrayList<String> getCleared() {
        return Cleared;
    }

    public ArrayList<Boolean> getClearedBool() {
        return ClearedBool;
    }
}