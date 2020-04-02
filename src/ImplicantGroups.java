import java.util.ArrayList;

public class ImplicantGroups implements Comparable<ImplicantGroups>{
    public int oneCount;
    public ArrayList<Implicant>implicantArrayList;

    public ImplicantGroups(int oneCount, ArrayList<Implicant> implicantArrayList) {
        this.oneCount = oneCount;
        this.implicantArrayList = implicantArrayList;
    }

    @Override
    public int compareTo(ImplicantGroups o) {
        return this.oneCount - o.oneCount;
    }
}
