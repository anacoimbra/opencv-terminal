/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author aninh
 */
public class Trabalho1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Scanner ler = new Scanner(System.in);
        int questao = 0;
        do{
            System.out.println("Escolha a questão (1,2 ou 3) ou 0 para finalizar: ");
            questao = ler.nextInt();
        }while(questao <= 0 || questao > 3);

        switch(questao){
            case 1:
                Questao1 q1 = new Questao1();
                break;
            case 2:
                Questao2 q2 = new Questao2();
                break;
            case 3:
                Questao3 q3 = new Questao3();
                break;
        }
    }

}
