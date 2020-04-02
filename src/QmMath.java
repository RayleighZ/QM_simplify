import java.util.ArrayList;
import java.util.Collections;

/**
 * 本工具类的接口如下
 * 1 输入：最小项集合
 * 2 输出：最简的表达式(String类型)
 */
public class QmMath {
    public ArrayList<Implicant> implicantArrayList;
    private ArrayList<Implicant> epiImplicantArrayList = new ArrayList<>();
    private ArrayList<ImplicantGroups> implicantGroupsArrayList = new ArrayList<>();
    private ArrayList<Implicant> finalArray = new ArrayList<>();

    public void setImplicantArrayList(ArrayList<Implicant> implicantArrayList) {
        this.implicantArrayList = implicantArrayList;
        simplifyMainFunction();
    }

    private void simplifyMainFunction() {
        divideIntoGroups();
        simplifyOneStage(implicantGroupsArrayList);
        judgeCovers();
        System.out.println("化简结果为：");
        for (int i = 0; i < finalArray.size(); i++) {
            Implicant im = finalArray.get(i);
            if (!im.isNCI && i != finalArray.size() - 1) {
                System.out.print(translate(im.doubleTypeString));
                System.out.print("+");
            } else {
                System.out.print(translate(im.doubleTypeString));
            }
        }
    }

    private void simplifyOneStage(ArrayList<ImplicantGroups> implicantGroupsArrayList) {
        int size = 1;
        while (size != 0) {
            ArrayList<ImplicantGroups> newImplicantGroupsArrayList = new ArrayList<>();
            Collections.sort(implicantGroupsArrayList);
            if (implicantGroupsArrayList.size() == 1) {
                epiImplicantArrayList.addAll(implicantGroupsArrayList.get(0).implicantArrayList);
            }
            for (int i = 0; i < implicantGroupsArrayList.size() - 1; i++) {
                ImplicantGroups now = implicantGroupsArrayList.get(i);
                ImplicantGroups next = implicantGroupsArrayList.get(i + 1);
                if (now.oneCount - next.oneCount == -1) {
                    if (i != implicantGroupsArrayList.size() - 2) {
                        ImplicantGroups implicantGroups = simplifyGroups(now, next, true);
                        if (implicantGroups != null)
                            newImplicantGroupsArrayList.add(implicantGroups);
                    } else {
                        ImplicantGroups implicantGroups = simplifyGroups(now, next, false);
                        if (implicantGroups != null)
                            newImplicantGroupsArrayList.add(implicantGroups);
                    }
                }
            }
            size = newImplicantGroupsArrayList.size();
            implicantGroupsArrayList = new ArrayList<> (newImplicantGroupsArrayList);
        }
    }

    //分组
    private void divideIntoGroups() {
        ArrayList<Implicant> implicantArrayListForNow = new ArrayList<>();
        Collections.sort(implicantArrayList);//进行集合的按照1的个数排序
        int curOneCount = implicantArrayList.get(0).oneCount;
        int count = 0;
        for (Implicant implicant : implicantArrayList) {
            count++;
            if (curOneCount == implicant.oneCount) {
                //System.out.println(curOneCount +" "+ implicant.oneCount);
                implicantArrayListForNow.add(implicant);
                if (count == implicantArrayList.size()) {
                    ArrayList<Implicant> arrayListForAdd = new ArrayList<>(implicantArrayListForNow);//= (ArrayList<Implicant>) implicantArrayListForNow.clone();
                    ImplicantGroups ig = new ImplicantGroups(curOneCount, arrayListForAdd);
                    implicantGroupsArrayList.add(ig);
                    implicantArrayListForNow.clear();
                }
            } else {
                ArrayList<Implicant> arrayListForAdd = new ArrayList<>(implicantArrayListForNow);
                ImplicantGroups ig = new ImplicantGroups(curOneCount, arrayListForAdd);
                implicantGroupsArrayList.add(ig);
                implicantArrayListForNow.clear();
                curOneCount = implicant.oneCount;
                implicantArrayListForNow.add(implicant);
                //System.out.println(implicantArrayListForNow.size());
                if (count == implicantArrayList.size()) {
                    ArrayList<Implicant> arrayListForAdd2 = new ArrayList<>(implicantArrayListForNow);
                    ImplicantGroups ig2 = new ImplicantGroups(curOneCount, arrayListForAdd2);
                    implicantGroupsArrayList.add(ig2);
                    implicantArrayListForNow.clear();
                }
            }
        }
    }

    private boolean canSimplify(Implicant implicantA, Implicant implicantB) {
        int difCount = 0;
        for (int i = 0; i < implicantA.doubleTypeString.length(); i++) {
            if (implicantA.doubleTypeString.charAt(i) != implicantB.doubleTypeString.charAt(i)) {
                difCount++;
            }
        }
        return difCount == 1;
    }

    //两个最小项之间的化简
    private Implicant simplify(Implicant ia, Implicant ib) {
        Implicant resultImplicant = new Implicant();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ia.doubleTypeString.length(); i++) {
            if (ia.doubleTypeString.charAt(i) == ib.doubleTypeString.charAt(i)) {
                sb.append(ib.doubleTypeString.charAt(i));
            } else {
                sb.append('_');
            }
        }
        resultImplicant.setNCI(false);
        resultImplicant.setDoubleTypeString(sb.toString());
        resultImplicant.addAllCovers(ia.coversList);
        resultImplicant.addAllCovers(ib.coversList);
        return resultImplicant;
    }

    //两个相邻集合的化简
    private ImplicantGroups simplifyGroups(ImplicantGroups ig1, ImplicantGroups ig2, boolean hasNext) {
        ImplicantGroups resultImplicantGroups;
        ArrayList<Implicant> implicantArrayListTostore = new ArrayList<>();
        for (Implicant ia :
                ig1.implicantArrayList) {
            for (Implicant ib :
                    ig2.implicantArrayList) {
                if (canSimplify(ia, ib)) {
                    boolean canAdd = true;
                    Implicant r = simplify(ia, ib);
                    for (Implicant i :
                            implicantArrayListTostore) {
                        if (i.doubleTypeString.equals(r.doubleTypeString)) {
                            canAdd = false;
                            break;
                        }
                    }
                    if (canAdd) {
                        implicantArrayListTostore.add(r);
                    }
                    ib.setUsed(true);
                    ia.setUsed(true);
                }
            }
        }
        for (Implicant ia : ig1.implicantArrayList) {
            if (!ia.isUsed) {
                boolean canAdd = true;
                for (Implicant i :
                        epiImplicantArrayList) {
                    if (i.doubleTypeString.equals(ia.doubleTypeString)) {
                        canAdd = false;
                        break;
                    }
                }
                if (canAdd)
                    epiImplicantArrayList.add(ia);
            }
        }
        if (!hasNext) {
            for (Implicant ib : ig2.implicantArrayList) {
                if (!ib.isUsed) {
                    boolean canAdd = true;
                    for (Implicant i :
                            epiImplicantArrayList) {
                        if (i.doubleTypeString.equals(ib.doubleTypeString)) {
                            canAdd = false;
                            break;
                        }
                    }
                    if (canAdd)
                        epiImplicantArrayList.add(ib);
                }
            }
        }
        if (!implicantArrayListTostore.isEmpty()) {
            resultImplicantGroups = new ImplicantGroups(implicantArrayListTostore.get(0).oneCount, implicantArrayListTostore);
            return resultImplicantGroups;
        } else {
            return null;
        }
    }

    private String translate(String doubleString) {
        char c = 'A';
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < doubleString.length(); i++) {
            if (doubleString.charAt(i) == '1') {
                sb.append(c++);
            } else if (doubleString.charAt(i) == '0') {
                sb.append(c++);
                sb.append("'");
            } else {
                c++;
            }
        }
        return sb.toString();
    }

    //最终的最小项覆盖算法
    private void judgeCovers() {
        //先对每一个最小项进行cover标记
        //填充本源蕴含图
        implicantArrayList.removeIf(im -> im.isNCI);
        int imCount = implicantArrayList.size();
        int epiCount = epiImplicantArrayList.size();//这个与epiArray的位置一一对应
        String[][] picMap = new String[imCount][epiCount];
        for (int i = 0; i < epiCount; i++) {
            Implicant implicant = epiImplicantArrayList.get(i);//被提取cover的最小项
            ArrayList<String> covers = implicant.coversList;
            for (String cover : covers) {
                picMap[positionTranslate(cover)][i] = "X";
            }
        }
        //目前已经完成绘图，下一步将进行epi的检索
        int xCount = 0;
        int yPosition = 0;//默认为0

        for (int i = 0; i < imCount; i++) {
            for (int j = 0; j < epiCount; j++) {
                if (picMap[i][j] != null)
                    if (picMap[i][j].equals("X")) {
                        xCount++;
                        yPosition = j;
                    }
            }
            if (xCount == 1) {
                Implicant implicant = epiImplicantArrayList.get(yPosition);
                addOnlyOnce(finalArray, implicant);
                implicant.setEpi(true);
            }
            xCount = 0;
        }

        //目前已经完成本质本源蕴含项的确定，下面对余下的蕴含项进行包含查找
        //下面对于被本质本源包含的进行删除
        boolean[] isCovered = new boolean[imCount];
        ArrayList<Implicant> lastArray = new ArrayList<>();//用来存放剩余的本源蕴含项
        ArrayList<Implicant> lastIm = new ArrayList<>();//用来放剩余的没有被cover的最小项
        for (Implicant i : epiImplicantArrayList) {
            if (i.isEpi) {
                for (String cover : i.coversList) {
                    isCovered[positionTranslate(cover)] = true;
                }
            }
        }

        //下面对于余下的最小项项进行cover操作
        //基本思路：直接对余下的所有组合遍历（包括自己与自己组合的情况）看看那种可以搞定
        //下面先把剩余本源蕴含项和没有被cover的最小项的取出来

        for (int i = 0; i < imCount; i++) {
            if (!isCovered[i]) {
                lastIm.add(implicantArrayList.get(i));
                for (int j = 0; j < epiCount; j++) {
                    if (picMap[i][j] != null)
                        if (picMap[i][j].equals("X")) {
                            addOnlyOnce(lastArray, epiImplicantArrayList.get(j));
                        }
                }
            }
        }

        //现在要对lastArray里面的元素取集合
        ArrayList<ImplicantGroups> collections = new ArrayList<>();//这里稍微借助一些IG,oneCount表示含有的元素种类数

        for (int i = 0; i < lastArray.size(); i++) {
            for (Implicant implicant : lastArray) {
                ArrayList<Implicant> collection = new ArrayList<>();//单个集合
                addOnlyOnce(collection, lastArray.get(i));
                addOnlyOnce(collection, implicant);
                collections.add(new ImplicantGroups(collection.size(), collection));
            }
        }



        //现在拿到了各种组合的collections，接下来要进行包含测试
        Collections.sort(collections);

        for (ImplicantGroups ig : collections) {
            boolean isFinal = true;
            ArrayList<String> names = new ArrayList<>();
            for (Implicant i : ig.implicantArrayList) {
                names.addAll(i.coversList);
            }//读取集合中包含的所有最小项

            boolean[] contains = new boolean[lastIm.size()];
            int counter = -1;
            for (Implicant im : lastIm) {
                counter ++;
                for (String name : names) {
                    //如果所包含的最小项里面不含有im的名字
                    if (name.equals(im.doubleTypeString)) {
                        contains[counter] = true;
                        break;
                    }
                }

            }
            for(boolean contain : contains){
                if (!contain) {
                    isFinal = false;
                    break;
                }
            }

            if (isFinal) {
                finalArray.addAll(ig.implicantArrayList);
                break;
            }
        }
    }


    private void addOnlyOnce(ArrayList<Implicant> implicantArrayList, Implicant ia) {
        boolean canAdd = true;
        //System.out.println(implicantArrayList.size());
        for (Implicant i :
                implicantArrayList) {
            //System.out.println("本源的"+i.doubleTypeString+ "等待加入的"+ia.doubleTypeString);
            if (i.doubleTypeString.equals(ia.doubleTypeString)) {
                //System.out.println("本源的"+i.doubleTypeString+ "等待加入的"+ia.doubleTypeString+"相等");
                canAdd = false;
                break;
            }
        }
        if (canAdd) {
            //System.out.println("能添加");
            implicantArrayList.add(ia);
        }
    }

    //这个算法的目的是给出最小项表达式在最小项里面的排位，也就是最终map的横坐标
    private int positionTranslate(String doubleTypeString) {
        ArrayList<String> implicantList = new ArrayList<>();
        for (Implicant implicant : implicantArrayList) {
            implicantList.add(implicant.doubleTypeString);
        }

        for (int i = 0; i <= implicantList.size(); i++) {
            if (doubleTypeString.equals(implicantList.get(i))) {
                return i;
            }
        }
        return -1;
    }
}
