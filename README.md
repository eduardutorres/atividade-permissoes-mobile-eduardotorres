# Tier List — Versão com Permissão Android

## Descrição
Aplicativo Android de Tier List que consome a iTunes Search API para buscar músicas de um artista e classificá-las nos níveis S, A, B, C, D e E. Nesta versão, foi adicionada a funcionalidade de exportar a Tier List como uma imagem para Story (1080x1920px) para a galeria do dispositivo, utilizando permissão Android de armazenamento externo.

## Relação com a atividade anterior
Na atividade anterior, o app permitia buscar músicas de um artista pela iTunes Search API e organizá-las em uma Tier List com 6 níveis, exibindo capa, nome e álbum de cada faixa. Nesta versão, foi adicionado o botão "Exportar Tier List", que gera uma imagem com a Tier List montada pelo usuário e salva diretamente na galeria do dispositivo — funcionalidade que exige permissão Android de armazenamento.

## API utilizada
- **Nome da API:** iTunes Search API
- **Endpoint utilizado:** [`https://itunes.apple.com/search`](https://itunes.apple.com/search)
- **Dados exibidos no app:** Nome da música (trackName), nome do artista (artistName), álbum (collectionName) e capa do álbum (artworkUrl100)

## Permissão Android utilizada
- **Permissão escolhida:** `WRITE_EXTERNAL_STORAGE` (Android até 9), `READ_EXTERNAL_STORAGE` (Android 10 a 12) e `READ_MEDIA_IMAGES` (Android 13+)
- **Onde ela foi declarada no Manifest:** No arquivo `AndroidManifest.xml`, antes da tag `<application>`
- **Por que essa permissão é necessária para o app:** Para salvar a imagem gerada da Tier List na galeria pública do dispositivo. Sem essa permissão, o app não consegue gravar o arquivo de imagem no armazenamento externo nas versões do Android que exigem autorização explícita.
- **Em qual momento do fluxo ela é solicitada ao usuário:** Quando o usuário toca em "Exportar Tier List", o app exibe um diálogo explicando a necessidade da permissão e, ao confirmar, solicita a autorização caso ainda não tenha sido concedida.

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

## Fluxo da permissão
1. **A permissão já foi concedida:** O app gera a imagem e salva diretamente na galeria, exibindo "Story salvo na Galeria!".
2. **O usuário concede a permissão:** Após aceitar o diálogo do sistema, o app executa a exportação normalmente e salva a imagem na galeria.
3. **O usuário nega a permissão:** O app exibe um diálogo informando que sem a permissão a exportação não é possível, mas o restante do app continua funcionando normalmente para busca e montagem da Tier List.

## Funcionalidades
- Consumo de API pública (iTunes Search API)
- Validação de entrada (campo de artista não pode estar vazio)
- Funcionalidade com permissão Android (exportar Tier List para a galeria)
- Tratamento de permissão concedida (gera e salva a imagem)
- Tratamento de permissão negada (diálogo explicativo, app não quebra)
- Exibição de feedback ao usuário em todas as etapas (Toasts e diálogos)

## Tecnologias utilizadas
- Kotlin
- Android Studio
- XML
- Volley (requisições HTTP) e Glide (carregamento de imagens)
- iTunes Search API
- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES` (permissão Android de armazenamento)

## Como executar o projeto
1. Clonar este repositório.
2. Abrir o projeto no Android Studio.
3. Aguardar a sincronização do Gradle.
4. Executar em emulador ou dispositivo físico.
5. Testar a funcionalidade de API: digitar um artista e tocar em "Buscar Músicas".
6. Testar a funcionalidade que solicita permissão: adicionar músicas aos tiers e tocar em "Exportar Tier List".

## Prints do aplicativo
<table align="center">
  <tr>
    <td align="center" width="200">
      <img src="https://github.com/user-attachments/assets/784efa47-952b-4714-a2f3-9b90cb4bc047" width="200" />
      <p><b>1. Tela principal</b></p>
    </td>
    <td align="center" width="200">
      <img src="https://github.com/user-attachments/assets/aded11a1-ba21-48cb-89e3-a20c423d94d4" width="200" />
      <p><b>2. Resultados</b></p>
    </td>
  </tr>
  <tr>
    <td align="center" width="200">
      <img src="https://github.com/user-attachments/assets/8c909e7d-1807-4dbb-a47d-c3add0515853" width="200" />
      <p><b>3. Adicionando ao tier</b></p>
    </td>
    <td align="center" width="200">
      <img src="https://github.com/user-attachments/assets/dea61dc9-3291-4253-83e9-3390a5463aa7" width="200" />
      <p><b>4. Tier List montada</b></p>
    </td>
  </tr>
  <tr>
    <td align="center" width="200">
      <img src="https://github.com/user-attachments/assets/a3db8b67-e523-42de-aace-2b17b3611b5b" width="200" />
      <p><b>5. Diálogo de exportação</b></p>
    </td>
    <td align="center" width="200">
      <img src="https://github.com/user-attachments/assets/6305d67e-0ff3-4c9f-ae3a-5deddde29ae0" width="200" />
      <p><b>6. Permissão solicitada</b></p>
    </td>
  </tr>
  <tr>
    <td align="center" width="200">
      <img src="https://github.com/user-attachments/assets/2cc9cf45-4133-43ec-8bf0-da02afdc8ef2" width="200" />
      <p><b>7. Story exportado</b></p>
    </td>
  </tr>
</table>

## Autor
Eduardo Vieira Torres dos Santos.
