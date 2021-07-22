package Gerir;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Classe que torna possivel ter o tempo de execução do programa.
 */

public class TempoExecucao {

    /**Tempo de execução de um programa
     */

    private final LocalDateTime tempoInicio = LocalDateTime.now();

    /**Método para calcular diferença de tempo na execução (tempo final - tempo inicial).
     */

    public void calcularTempoExecucao(){

        DateTimeFormatter x = DateTimeFormatter.ofPattern("EEEE; yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Duration duration = Duration.between(tempoInicio.atZone(ZoneId.systemDefault()).toInstant(), Instant.now());

        System.out.println("Inicio: "+tempoInicio.format(x)+"\n"+"Fim: "+LocalDateTime.now().format(x)+"\n"+
                "Tempo de execucao: "+duration.toMillis()+ "milisegundos ( Segundos = "+ duration.toSecondsPart()+
                "; minutos = "+duration.toMinutesPart()+"; horas = "+duration.toHoursPart() +" )");

    }

}

