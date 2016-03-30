/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author aninh
 */
public class Questao1 {

    Scanner in;

    String img1;
    String img2;
    Mat image1;
    Mat image2;

    Mat image1bin;
    Mat image2bin;

    Mat output;

    int op = -1;

    public Questao1(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        this.img1 = "lena.jpg";
        this.img2 = "baboon.jpg";

        this.image2 = Imgcodecs.imread(img2);
        this.image1 = Imgcodecs.imread(img1);

        this.image1bin = new Mat();
        this.image2bin = new Mat();

        this.output = new Mat(256,256,CvType.CV_8UC3);

        showResult(img1);
        showResult(img2);

        in = new Scanner(System.in);
        do{
            System.out.println("Escolha a operação: ");
            System.out.println("1 - AND");
            System.out.println("2 - OR");
            System.out.println("3 - XOR");
            System.out.println("4 - NOT");
            System.out.println("5 - SOMA");
            System.out.println("6 - SUBTRAÇÃO");
            System.out.println("7 - MULTIPLICAÇÃO");
            System.out.println("8 - DIVISÃO");
            System.out.println("0 - SAIR");

            while((op = in.nextInt()) != 0){
                realizarOperacoes();
            }
            if(op == 0){
                return;
            }
        }while(op < 0 || op > 8);
    }

    void realizarOperacoes(){
        switch(op){
            case 1:
                and();
                break;
            case 2:
                or();
                break;
            case 3:
                xor();
                break;
            case 4:
                not();
                break;
            case 5:
                soma();
                break;
            case 6:
                subtracao();
                break;
            case 7:
                multiplicacao();
                break;
            case 8:
                divisao();
                break;
        }
    }

    void imagemBinaria(){
        Imgproc.cvtColor(image1, image1bin, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(image2, image2bin, Imgproc.COLOR_RGB2GRAY);

        for(int i = 0; i < image1bin.rows(); i++){
            for(int j = 0; j < image1bin.cols(); j++){
                if(image1bin.get(i, j)[0] > 128){
                    image1bin.put(i, j, 0);
                }else{
                    image1bin.put(i, j, 1);
                }
                if(image2bin.get(i, j)[0] > 128){
                    image2bin.put(i, j, 0);
                }else{
                    image2bin.put(i, j, 1);
                }
            }
        }
    }

    void normalizarBinario(){
        for(int i = 0; i < output.rows(); i++){
            for(int j = 0; j < output.cols(); j++){
                if(output.get(i, j)[0] > 0){
                    output.put(i, j, 0);
                }else{
                    output.put(i, j, 255);
                }
            }
        }
    }

    void and(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        imagemBinaria();
        Core.bitwise_and(image1bin, image2bin, output);
        normalizarBinario();

        Imgcodecs.imwrite("and.jpg", output);
        showResult("and.jpg");
    }

    void or(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        imagemBinaria();
        Core.bitwise_or(image1bin, image2bin, output);
        normalizarBinario();

        Imgcodecs.imwrite("or.jpg", output);
        showResult("or.jpg");
    }

    void xor(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        imagemBinaria();
        Core.bitwise_xor(image1bin, image2bin, output);
        normalizarBinario();

        Imgcodecs.imwrite("xor.jpg", output);
        showResult("xor.jpg");
    }

    void not(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        imagemBinaria();
        Core.bitwise_not(image2bin, output);
        normalizarBinario();

        Imgcodecs.imwrite("not.jpg", output);
        showResult("not.jpg");
    }

    void soma(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Core.add(image1, image2, output);
        Imgcodecs.imwrite("soma.jpg", output);
        showResult("soma.jpg");
    }

    void subtracao(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Core.subtract(image1, image2, output);
        Imgcodecs.imwrite("subtracao.jpg", output);
        showResult("subtracao.jpg");
    }

    void multiplicacao(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Core.multiply(image1, image2, output);
        Imgcodecs.imwrite("multiplicacao.jpg", output);
        showResult("multiplicacao.jpg");
    }

    void divisao(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Core.divide(image1, image2, output);
        Imgcodecs.imwrite("divisao.jpg", output);
        showResult("divisao.jpg");
    }

    public static void showResult(String url) {
        JFrame frame = new JFrame(url);
        frame.getContentPane().add(new JLabel(new ImageIcon(url)));
        frame.pack();
        frame.setVisible(true);
    }
}
