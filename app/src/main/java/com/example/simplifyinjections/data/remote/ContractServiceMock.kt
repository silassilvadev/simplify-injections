package com.example.simplifyinjections.data.remote

import com.example.simplifyinjections.data.Result
import com.example.simplifyinjections.domain.model.ContractResponse
import kotlinx.coroutines.delay
import java.lang.IllegalStateException

internal class ContractServiceMock : ContractService {

    private var attemptsMock = 0

    override suspend fun getContract(): Result<ContractResponse> {
        return when (attemptsMock) {
            0 -> {
                attemptsMock++
                delay(DELAY_TO_REMOTE_SIMULATE)
                Result.Error(NoSuchElementException("Erro ao buscar o contrato"))
            }
            1 -> {
                attemptsMock++
                delay(DELAY_TO_REMOTE_SIMULATE)
                Result.Error(NoSuchElementException("Sem conexão"))
            }
            2 -> {
                attemptsMock++
                delay(DELAY_TO_REMOTE_SIMULATE)
                Result.Success(ContractResponse("12345"))
            }
            else -> {
                attemptsMock = 0
                delay(DELAY_TO_REMOTE_SIMULATE)
                Result.Error(IllegalStateException())
            }
        }
    }

    companion object {
        private const val DELAY_TO_REMOTE_SIMULATE = 2000L
    }
}
