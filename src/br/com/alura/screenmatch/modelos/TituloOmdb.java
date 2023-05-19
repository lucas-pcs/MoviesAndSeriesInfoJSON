package br.com.alura.screenmatch.modelos;

import com.google.gson.annotations.Expose;

public record TituloOmdb(@Expose String title,@Expose String year,@Expose String runtime) {
}
