package com.digital_insurance_company.polizas_service.infraestructura.entrada.grpc;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.digital_insurance_company.polizas_service.dominio.Poliza;
import com.digital_insurance_company.polizas_service.dominio.PolizaNoEncontradaException;
import com.digital_insurance_company.polizas_service.dominio.puertos.PolizaUseCase;
import com.digital_insurance_company.polizas_service.grpc.ConsultaPolizaRequest;
import com.digital_insurance_company.polizas_service.grpc.ConsultaPolizaResponse;
import com.digital_insurance_company.polizas_service.grpc.PolizaConsultaGrpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

@Service
public class PolizaGrpcService extends PolizaConsultaGrpc.PolizaConsultaImplBase {

    private final PolizaUseCase polizaUseCase;

    public PolizaGrpcService(PolizaUseCase polizaUseCase) {
        this.polizaUseCase = polizaUseCase;
    }

    @Override
    public void consultarPoliza(ConsultaPolizaRequest request,
            StreamObserver<ConsultaPolizaResponse> responseObserver) {
        UUID polizaId;
        try {
            polizaId = UUID.fromString(request.getPolizaId());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("polizaId no es un UUID válido: " + request.getPolizaId())
                    .asRuntimeException());
            return;
        }

        ConsultaPolizaResponse respuesta;
        try {
            Poliza poliza = polizaUseCase.consultarPoliza(polizaId);
            respuesta = ConsultaPolizaResponse.newBuilder()
                    .setEncontrada(true)
                    .setEstado(poliza.estadoEn(LocalDate.now()).name())
                    .setValorAsegurado(poliza.valorAsegurado().monto().toPlainString())
                    .setFechaInicio(poliza.vigencia().fechaInicio().toString())
                    .setFechaFin(poliza.vigencia().fechaFin().toString())
                    .setClienteId(poliza.clienteId().toString())
                    .build();
        } catch (PolizaNoEncontradaException e) {
            respuesta = ConsultaPolizaResponse.newBuilder().setEncontrada(false).build();
        }

        responseObserver.onNext(respuesta);
        responseObserver.onCompleted();
    }
}
