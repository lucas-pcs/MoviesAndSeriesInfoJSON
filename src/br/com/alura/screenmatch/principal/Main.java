package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.modelos.Titulo;
import br.com.alura.screenmatch.modelos.TituloOmdb;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static final String API_KEY = "PUT YOUR OMDB KEY HERE";

    public static void main(String[] args) throws IOException, InterruptedException {
        // lista onde será guardado os titulos
        List<Titulo> listaDeTitulos = new ArrayList<>();

        // biblioteca responsável por converter um objeto para JSON e vice-versa
        Gson gson = createGsonObject();

        // controle de saida do looping
        boolean loopingExit = false;

        while(!loopingExit){

            String nomeDoFilme = getTitleName();

            // verifica se deve interromper o looping
            if(nomeDoFilme.equalsIgnoreCase("sair")){
                loopingExit = true;
                break;
            }

            String jsonResponse = getJsonFromOmdbAPI(nomeDoFilme);

            // instancia a classe Titulos com os objetos obtidos da classe Record
            TituloOmdb tituloOmdb = gson.fromJson(jsonResponse, TituloOmdb.class);
            Titulo titulo = new Titulo(tituloOmdb);

            listaDeTitulos.add(titulo);
        }

        writeJsonFile(listaDeTitulos, gson);

        // System.out.println(listaDeTitulos);
    }

    private static Gson createGsonObject() {
        // classe responsável por serializar e desserializar
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                // metódo do builder que será responsável por deixar no JSON apenas
                // os campos selecionados com a anotação ao invés de todos
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson;
    }

    private static void writeJsonFile(List<Titulo> listaDeTitulos, Gson gson) throws IOException {
        FileWriter writer = new FileWriter("lista_de_titulos.json");
        writer.write(gson.toJson(listaDeTitulos));
        writer.close();
    }

    private static String getTitleName() {
        // obtem nome do filme
        Scanner scan = new Scanner(System.in);
        System.out.println("Digite o nome do filme ou sair para encerrar a execução: ");
        String nomeDoFilme = scan.nextLine();
        return nomeDoFilme;
    }

    private static String getJsonFromOmdbAPI(String nomeDoFilme) throws IOException, InterruptedException {
        // monta endereço
        String endereco = ("http://www.omdbapi.com/?apikey=" + API_KEY + "&t=" + nomeDoFilme);

        // requisição http
        HttpClient client = HttpClient.newBuilder().build();

        // request criada fora do escolpo para ser utilizada posteriormente no HttpResponse
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder().uri(URI.create(endereco)).build();
        } catch (IllegalArgumentException e) {
            // em caso de falha, como um caracter de espaço no nome do titulo, substitui o " " por "+" e tenta novamente
            // montar o request
            endereco = ("http://www.omdbapi.com/?apikey=" + API_KEY + "&t=" + nomeDoFilme.replace(" ", "+"));
            request = HttpRequest.newBuilder().uri(URI.create(endereco)).build();
        }

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // json recebido de resposta
        String jsonResponse = response.body();
        return jsonResponse;
    }
}