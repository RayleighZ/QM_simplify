import java.util.ArrayList;
import java.util.Scanner;

public class QmMain {
    public static void main(String[] args) {
        QmMain qmMain = new QmMain();
        System.out.println("请输入变量个数");
        Scanner scanner = new Scanner(System.in);
        Implicant.numOfVariable = scanner.nextInt();
        scanner.nextLine();
        System.out.println("请输入最小项(用空格隔开,输入回车以结束,如果没有请输入一个空格)");
        String basicImplicantLine = scanner.nextLine();
        ArrayList<Implicant> implicantArrayList = new ArrayList<>();//用来储存最小项的集合
        if(!basicImplicantLine.equals(" "))
        qmMain.getImplicant(implicantArrayList,basicImplicantLine,false);
        System.out.println("请输入无关变量(用空格隔开,输入回车以结束,如果没有请输入一个空格)");
        String basicNciLine = scanner.nextLine();
        if(!basicNciLine.equals(" "))
        qmMain.getNCI(implicantArrayList,basicNciLine);
        //自此已经完成了信息的收集工作

        //开始准备进行QM算法的实现
        QmMath qmMath = new QmMath();
        qmMath.setImplicantArrayList(implicantArrayList);
    }

    //获取最小项的工具方法
    private void getImplicant(ArrayList<Implicant> implicantArrayList, String basicImplicantLine, boolean isNCI) {
        int begin = 0;
        int tail ;
        for (int i = 0; i < basicImplicantLine.length(); i++) {
            if (basicImplicantLine.charAt(i) == ' ') {
                tail = i;
                Implicant implicant = new Implicant();
                implicant.setDoubleTypeString(Integer.parseInt(basicImplicantLine.substring(begin, tail)));
                implicant.setNCI(isNCI);
                if(!isNCI)
                implicant.addCovers(implicant.doubleTypeString);
                implicantArrayList.add(implicant);
                begin = tail + 1;
            }
        }
        Implicant implicant = new Implicant();
        implicant.setDoubleTypeString(Integer.parseInt(basicImplicantLine.substring(begin)));
        implicant.setNCI(isNCI);
        if(!isNCI)
        implicant.addCovers(implicant.doubleTypeString);
        implicantArrayList.add(implicant);
    }

    // 获取无关变量的工具方法
    private void getNCI(ArrayList<Implicant>implicantArrayList,String nciLine){
        getImplicant(implicantArrayList,nciLine,true);
    }
}
