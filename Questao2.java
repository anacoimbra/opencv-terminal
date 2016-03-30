/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import static org.opencv.core.Core.normalize;
import static org.opencv.core.Core.randn;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author aninh
 */
public class Questao2 {

    Scanner in;

    String img;
    Mat image;

    Mat output;

    int ruido = -1;
    int filtro = -1;

    public Questao2(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this.in = new Scanner(System.in);
        this.output = new Mat(256,26,CvType.CV_8UC3);
        this.img = "lena.jpg";
        this.image = Imgcodecs.imread(img);

        showResult(img);

        do{
            System.out.println("Escolha o ruído a ser aplicado: ");
            System.out.println("1 - Gaussiano");
            System.out.println("2 - Sal e Pimenta");
            System.out.println("0 - Sair");

            while((ruido = in.nextInt()) != 0){
                aplicarRuidos();
            }
            if(ruido == 0){
                break;
            }
        }while(ruido < 0 || ruido > 8);

        do{
            System.out.println("Escolha o filtro a ser aplicado: ");
            System.out.println("1 - Média");
            System.out.println("2 - Mediana");
            System.out.println("3 - Gaussiano");
            System.out.println("4 - Máximo");
            System.out.println("5 - Mínimo");

            while((filtro = in.nextInt()) != 0){
                aplicarFiltros();
            }

            if(filtro == 0){
                return;
            }

        }while(filtro < 0 || filtro > 5);
    }

    void aplicarRuidos(){
        switch(ruido){
            case 1:
                ruidoGaussiano();
                break;
            case 2:
                ruidoSalPimenta();
                break;
        }
    }

    void ruidoGaussiano(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat original_Bgr = image.clone();

        // cria uma imagem e inicializa com valores aleatorios
        Mat mGaussian_noise = new Mat(original_Bgr.size(), original_Bgr.type());
        System.out.print("Valor Principal: ");
        int mean = in.nextInt();
        System.out.print("Desvio Padrão: ");
        int desv = in.nextInt();
        // randn(matriz destino, mean value, desvio padrao)
        randn(mGaussian_noise,mean,desv);

        // aplicacao do ruido: original(clone) + mGaussian_noise
        for(int m = 0; m < original_Bgr.rows(); m++){
            for(int n = 0; n < original_Bgr.cols(); n++){
                double[] val = new double[3];
                for(int i = 0; i < original_Bgr.get(m,n).length; i++){
                    val[i] = original_Bgr.get(m,n)[i] + mGaussian_noise.get(m, n)[i];
                }
                original_Bgr.put(m, n, val);
            }
        }

        // normalize(matriz entrada, matriz saida, valor minimo, valor maximo, tipo de normalizacao, tipo da imagem de saida)
        normalize(original_Bgr,original_Bgr,0, 255, Core.NORM_MINMAX, CvType.CV_8UC3);

        // salva resultado do ruido gaussiano na imagem "gaussian.jpg"
        Imgcodecs.imwrite("gaussian.jpg", original_Bgr);
        showResult("gaussian.jpg");
    }

    void ruidoSalPimenta(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // obtem clone da matriz original
        Mat saltPepper_img = image.clone();
        // cria matriz para o ruido e inicializa com valor aleatorios
        Mat mSaltPepper_noise = new Mat(saltPepper_img.size(), saltPepper_img.type());
        // randn(matriz destino, valor principal (espectativa), desvio padrao)
        randn(mSaltPepper_noise,0,255);
        System.out.print("Valor Mínimo: ");
        int min = in.nextInt();
        System.out.print("Valor Máximo: ");
        int max = in.nextInt();
        // utilizando da matriz de numeros aleatorios, verifica valores
        // muito baixos e os substituem por zero na matriz resultante (copia da original)
        // e os valores muito altos sao substituidos por 255
        for(int m = 0; m < saltPepper_img.rows(); m++){
            for(int n = 0; n < saltPepper_img.cols(); n++){
                double[] val = new double[3];
                if(mSaltPepper_noise.get(m,n)[0] < min && mSaltPepper_noise.get(m,n)[1] < min && mSaltPepper_noise.get(m,n)[2] < min){
                    for(int i = 0; i < saltPepper_img.get(m,n).length; i++){
                        val[i] = 0;
                    }
                    saltPepper_img.put(m, n, val);
                }
                if(mSaltPepper_noise.get(m,n)[0] > max && mSaltPepper_noise.get(m,n)[1] > max && mSaltPepper_noise.get(m,n)[2] > max){
                    for(int i = 0; i < saltPepper_img.get(m,n).length; i++){
                        val[i] = 255;
                    }
                    saltPepper_img.put(m, n, val);
                }
            }
        }

        // normalize(matriz entrada, matriz saida, valor minimo, valor maximo, tipo de normalizacao, tipo da imagem de saida)
        normalize(saltPepper_img,saltPepper_img,0, 255, Core.NORM_MINMAX, CvType.CV_8UC3);

        /**
         * Salva imagem resultante em saltapepper.jpg
         */
        Imgcodecs.imwrite("saltpepper.jpg", saltPepper_img);
        showResult("saltpepper.jpg");
    }

    void aplicarFiltros(){
        switch(filtro){
            case 1:
                media();
                break;
            case 2:
                mediana();
                break;
            case 3:
                gaussiano();
                break;
            case 4:
                maximo();
                break;
            case 5:
                minimo();
                break;
        }
    }

    String escolherUrl(){
        String url = "";
        /**
         * Seleciona o src da imagem a partir da escolha do usuario na Tela
         */
        System.out.println("Escolha em qual imagem aplicar o filtro: ");
        System.out.println("O - Original");
        System.out.println("G - Com filtro Gaussino");
        System.out.println("S - Com filtro Sal e Pimenta");

        char i = in.next().toUpperCase().charAt(0);

        switch(i){
            case 'O':
                url = img;
                break;
            case 'G':
                url = "gaussian.jpg";
                break;
            case 'S':
                url = "saltpepper.jpg";
                break;
            default:
                url = img;
                break;
        }
        return url;
    }

    void media(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String url = escolherUrl();

        /**
         * Transforma imagem em matriz para facilitar manipulacao
         */
        Mat img = Imgcodecs.imread(url);
        /**
         * Cria matriz de destino
         */
        Mat dst = new Mat();
        /**
         * Aplicacao do filtro da media
         * blur(matriz original, matriz destino, tamanho da mascara)
         */

        System.out.println("Escolha as dimensões da máscara: ");
        System.out.print("X: ");
        int mx = in.nextInt();
        System.out.print("Y: ");
        int my = in.nextInt();

        Imgproc.blur(img, dst, new Size(mx,my));
        /**
         * Salva o resultado em media.jpg
         */
        Imgcodecs.imwrite("media.jpg", dst);
        showResult("media.jpg");
    }

    void mediana(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String url = escolherUrl();
        /**
         * Transforma imagem em matriz para facilitar manipulacao
         */
        Mat img = Imgcodecs.imread(url);
        /**
         * Cria matriz de destino
         */
        Mat dst = new Mat();
        /**
         * Aplica filtro da mediana
         * medianBlur(imagem original, imagem destino, ksize)
         */
        System.out.print("K-size: ");
        int ksize = in.nextInt();
        Imgproc.medianBlur(img, dst,ksize);
        /**
         * Salva resultado em mediana.jpg
         */
        Imgcodecs.imwrite("mediana.jpg", dst);
        showResult("mediana.jpg");
    }

    void gaussiano(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String url = escolherUrl();

        /**
         * Transforma imagem em matriz para facilitar manipulacao
         */
        Mat img = Imgcodecs.imread(url);
        /**
         * Cria matriz de destino
         */
        Mat dst = new Mat();
        /**
         * Aplica o filtro gaussiano
         * GaussianBlur(imagem original, imgagem destino, tamanho da mascara, sygmaX, sygmaY)
         */
        System.out.println("Dimensões da máscara: ");
        System.out.print("X: ");
        int mx = in.nextInt();
        System.out.print("Y: ");
        int my = in.nextInt();

        System.out.println("Valores Sygma: ");
        System.out.print("X: ");
        int sx = in.nextInt();
        System.out.print("Y: ");
        int sy = in.nextInt();
        Imgproc.GaussianBlur(img, dst,new Size(mx,my),sx,sy);
        /**
         * Salva resultado em gaussian-blur.jpg
         */
        Imgcodecs.imwrite("gaussian-blur.jpg", dst);
        showResult("gaussian-blur.jpg");
    }

    void maximo(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String url = escolherUrl();
        /**
         * Transforma imagem em matriz para facilitar manipulacao
         */
        Mat img = Imgcodecs.imread(url);
        /**
         * Cria matriz destinoo
         */
        Mat dst = new Mat();
        /**
         * Cria imagem com o valor 1 em todos os pixels de
         * tamnho escolhido pelo usuario
         */
        System.out.println("Digite as dimensões da máscara: ");
        System.out.print("X: ");
        int mx = in.nextInt();
        System.out.print("Y: ");
        int my = in.nextInt();
        Mat one = Mat.ones(mx,my, CvType.CV_32F);
        /**
         * Aplica o filtro maximo utilizando a matriz one como mascara
         * dilate(imagem original, imagem destino, mascara)
         */
        Imgproc.dilate(img, dst, one);
        /**
         * Salva o resultado na matriz maximo.jpg
         */
        Imgcodecs.imwrite("maximo.jpg", dst);
        showResult("maximo.jpg");
    }

    void minimo(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String url = escolherUrl();
        /**
         * Transforma imagem em matriz para facilitar manipulacao
         */
        Mat img = Imgcodecs.imread(url);
        /**
         * Cria matriz de destino
         */
        Mat dst = new Mat();
        /**
         * Cria matriz com o valor 1 em todos os pixels de
         * tamanho definido pelo usuario
         */
        System.out.println("Digite as dimensões da máscara: ");
        System.out.print("X: ");
        int mx = in.nextInt();
        System.out.print("Y: ");
        int my = in.nextInt();
        Mat one = Mat.ones(mx, my, CvType.CV_32F);
        /**
         * Aplica o filtro minimo
         * erode(imagem original, imagem destino, matriz de mascara)
         */
        Imgproc.erode(img, dst, one);
        /**
         * Salva resultado em minimo.jpg
         */
        Imgcodecs.imwrite("minimo.jpg", dst);
        showResult("minimo.jpg");
    }

    public static void showResult(String url) {
        JFrame frame = new JFrame(url);
        frame.getContentPane().add(new JLabel(new ImageIcon(url)));
        frame.pack();
        frame.setVisible(true);
    }

}
