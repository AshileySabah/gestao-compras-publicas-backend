package br.fatec.compraspublicas.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.fatec.compraspublicas.dto.ErroResposta;

@RestControllerAdvice
public class TratadorDeErros {

	private static final Logger log = LoggerFactory.getLogger(TratadorDeErros.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroResposta> tratarDadosInvalidos(MethodArgumentNotValidException excecao) {
		List<ErroResposta.Campo> campos = excecao.getBindingResult().getFieldErrors().stream()
				.map(erro -> new ErroResposta.Campo(erro.getField(), erro.getDefaultMessage()))
				.toList();
		return responder(HttpStatus.BAD_REQUEST, new ErroResposta(400, "Dados inválidos. Corrija os campos indicados.", campos));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErroResposta> tratarCorpoIlegivel(HttpMessageNotReadableException excecao) {
		return responder(HttpStatus.BAD_REQUEST, new ErroResposta(400, "Requisição com formato inválido. Confira datas (aaaa-mm-dd), números e modalidade."));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErroResposta> tratarParametroInvalido(MethodArgumentTypeMismatchException excecao) {
		return responder(HttpStatus.BAD_REQUEST, new ErroResposta(400, "Valor inválido para o parâmetro '" + excecao.getName() + "'."));
	}

	@ExceptionHandler(ArquivoInvalidoException.class)
	public ResponseEntity<ErroResposta> tratarArquivoInvalido(ArquivoInvalidoException excecao) {
		return responder(HttpStatus.BAD_REQUEST, new ErroResposta(400, excecao.getMessage()));
	}

	@ExceptionHandler({ MissingServletRequestPartException.class, MultipartException.class })
	public ResponseEntity<ErroResposta> tratarEnvioSemArquivo(Exception excecao) {
		return responder(HttpStatus.BAD_REQUEST, new ErroResposta(400, "Envie o arquivo ZIP no campo 'arquivo' (formulário multipart)."));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErroResposta> tratarArquivoGrandeDemais(MaxUploadSizeExceededException excecao) {
		return responder(HttpStatus.PAYLOAD_TOO_LARGE, new ErroResposta(413, "O arquivo enviado passa do limite de 50 MB."));
	}

	@ExceptionHandler(RegraNegocioException.class)
	public ResponseEntity<ErroResposta> tratarRegraNegocio(RegraNegocioException excecao) {
		return responder(HttpStatus.BAD_REQUEST, new ErroResposta(400, excecao.getMessage()));
	}

	@ExceptionHandler({ RecursoNaoEncontradoException.class, NoResourceFoundException.class })
	public ResponseEntity<ErroResposta> tratarNaoEncontrado(Exception excecao) {
		String mensagem = excecao instanceof RecursoNaoEncontradoException ? excecao.getMessage() : "Endereço não encontrado.";
		return responder(HttpStatus.NOT_FOUND, new ErroResposta(404, mensagem));
	}

	@ExceptionHandler(RegistroDuplicadoException.class)
	public ResponseEntity<ErroResposta> tratarDuplicado(RegistroDuplicadoException excecao) {
		return responder(HttpStatus.CONFLICT, new ErroResposta(409, excecao.getMessage()));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErroResposta> tratarConflitoNoBanco(DataIntegrityViolationException excecao) {
		return responder(HttpStatus.CONFLICT, new ErroResposta(409, "A operação conflita com um registro já existente."));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErroResposta> tratarErroInesperado(Exception excecao) {
		log.error("Erro inesperado", excecao);
		return responder(HttpStatus.INTERNAL_SERVER_ERROR, new ErroResposta(500, "Erro inesperado no servidor. Tente novamente."));
	}

	private ResponseEntity<ErroResposta> responder(HttpStatus status, ErroResposta erro) {
		return ResponseEntity.status(status).body(erro);
	}
}
