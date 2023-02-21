package hw1;

import java.util.Scanner;

public class Surucu {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("SURUCU NO : ");
        int num = input.nextInt();
        System.out.println();

        if (num == 0) {
            job j0 = new job(0, 0, "H", 3);
            job j1 = new job(0, 1, "H", 2);
            job j2 = new job(0, 2, "M", 3);
            job j3 = new job(3, 3, "H", 3);

            Scheduler programlayici = new Scheduler();
            programlayici.setResourcesCount(2);
            programlayici.add(j0);
            programlayici.add(j1);
            programlayici.add(j2);
            programlayici.add(j3);
            programlayici.run();
            System.out.println("-------");
            programlayici.utilization(1); // kaynak 1, toplam 6 birim çalışmış ve arada boş kaldığı zaman              // olmamış, 6/6 = 1
            System.out.println("-------");
            programlayici.resourceExplorer(2);
            System.out.println("-------");
            programlayici.jobExplorer(j3);
        }
        /* OUTPUT:
        Zaman R1 R2
            0 J0 J1
            1 J0 J1
            2 J0 J2
            3 J3 J2
            4 J3 J2
            5 J3
        --------------------
        R1 verim: 1.00
        --------------------
        R2: (1,1,0), (2,4,2)
        --------------------
        islemno kaynak baslangic bitis gecikme
        3       R1     3         5     0
        */

        if (num == 1) {
            Scheduler schedular = new Scheduler();

            job J0 = new job(0, 0, "H", 3);
            job J1 = new job(0, 1, "M", 2);
            job J2 = new job(0, 2, "H", 3);
            job J3 = new job(0, 3, "H", 3);
            job J4 = new job(0, 4, "M", 2);
            job J5 = new job(0, 5, "H", 3);
            job J6 = new job(3, 6, "H", 3);
            job J7 = new job(4, 7, "L", 5);
            job J8 = new job(4, 8, "H", 4);
            job J9 = new job(9, 9, "M", 4);
            job J10 = new job(10, 10, "L", 2);

            schedular.setResourcesCount(5);
            schedular.add(J0);
            schedular.add(J1);
            schedular.add(J2);
            schedular.add(J3);
            schedular.add(J4);
            schedular.add(J5);
            schedular.add(J6);
            schedular.add(J7);
            schedular.add(J8);
            schedular.add(J9);
            schedular.add(J10);
            schedular.run();
            System.out.println();
            System.out.println("-------");
            schedular.utilization(1);
            schedular.utilization(2);
            schedular.utilization(3);
            schedular.utilization(4);
            schedular.utilization(5);
            System.out.println("-------");
            System.out.println("RX : (id,end,delay)");
            schedular.resourceExplorer(1);
            schedular.resourceExplorer(2);
            schedular.resourceExplorer(3);
            schedular.resourceExplorer(4);
            schedular.resourceExplorer(5);
            System.out.println("-------");
            schedular.jobExplorer(J0);
            schedular.jobExplorer(J1);
            schedular.jobExplorer(J2);
            schedular.jobExplorer(J3);
            schedular.jobExplorer(J4);
            schedular.jobExplorer(J5);
            schedular.jobExplorer(J6);
            schedular.jobExplorer(J7);
            schedular.jobExplorer(J8);
            schedular.jobExplorer(J9);
            schedular.jobExplorer(J10);
        }
    }
}