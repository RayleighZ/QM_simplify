import java.util.ArrayList;

public class Implicant implements Comparable<Implicant>{
    public static int numOfVariable;
    public int oneCount;
    public String doubleTypeString;
    public boolean isUsed = false;
    public boolean isNCI;
    public boolean isEpi = false;//是否为本质本源蕴含项
    public ArrayList<String> coversList = new ArrayList<>();

    //设置二进制形态下的最小项表示
    //输入代号自动计算其二进制表示
    public void setDoubleTypeString(int positionInMap) {
        String basicString = Integer.toBinaryString(positionInMap);

        //以下操作为计算其中1的个数
        int oneCount = 0;
        for (int i = 0; i < basicString.length(); i++) {
           if (basicString.charAt(i) == '1'){
               oneCount ++;
           }
        }
        this.oneCount = oneCount;

        //以下操作为补齐前导零
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numOfVariable - basicString.length(); i++) {
            sb.append(0);
        }
        sb.append(basicString);
        this.doubleTypeString = sb.toString();
    }

    public void setDoubleTypeString(String doubleTypeString) {
        this.doubleTypeString = doubleTypeString;
        int oneCount = 0;
        for (int i = 0; i < doubleTypeString.length(); i++) {
            if (doubleTypeString.charAt(i) == '1'){
                oneCount ++;
            }
        }
        this.oneCount = oneCount;
    }

    //设置是否为本质本源蕴含项
    public void setUsed(boolean used) {
        this.isUsed = used;
    }

    //设置是否为无关项
    public void setNCI(boolean NCI) {
        this.isNCI = NCI;
    }

    //设置这个本源蕴含项所包含的最小项
    public void addCovers(String farther){
        boolean canAdd = true;
        for (String s : this.coversList) {
            if (farther.equals(s)) {
                canAdd = false;
                break;
            }
        }
        if(canAdd)
            this.coversList.add(farther);
    }

    public void addAllCovers(ArrayList<String>coversList){
        this.coversList.addAll(coversList);
    }

    public void setEpi(boolean epi) {
        isEpi = epi;
    }

    @Override
    public int compareTo(Implicant implicant) {
        return this.oneCount - implicant.oneCount;
    }
}
