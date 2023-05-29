package me.dio.credit.application.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import me.dio.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*


@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CustomerServiceTest {
    @MockK lateinit var customerRepository: CustomerRepository
    @InjectMockKs lateinit var customerService: CustomerService

    @Test
    fun `should create customer`(){
        //Given
        val fakeCustomer: Customer = buildCustomer()
        every {customerRepository.save( any())} returns fakeCustomer
        //When
        val actual: Customer = customerService.save(fakeCustomer)
        //Then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify(exactly = 1) {customerRepository.save(fakeCustomer)}
    }

    @Test
    fun `should find customer by id`(){
        //GIVEN
        val fakeId: Long = Random().nextLong()
        val fakeCustomer: Customer = buildCustomer(id =fakeId)
        every{customerRepository.findById(fakeId)} returns Optional.of(fakeCustomer)
        //WHEN
        val actual: Customer = customerService.findById(fakeId)
        //THEN
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isExactlyInstanceOf(Customer::class.java)
        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `should not find customer by invalid id and throw BusinessException`(){
        //GIVEN
        val fakeId: Long = Random().nextLong()
        every { customerRepository.findById(fakeId) } returns Optional.empty()
        //WHEN

        //THEN
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { customerService.findById(fakeId) }
            .withMessage("Id $fakeId not found")
        verify ( exactly =1 ) {customerRepository.findById(fakeId)}
    }

    @Test
    fun `should delete customer by id`(){
        //GIVEN
        val fakeId: Long = Random().nextLong()
        val fakeCustomer: Customer = buildCustomer()
        every{customerRepository.findById(fakeId)} returns Optional.of(fakeCustomer)
        every { customerRepository.delete(fakeCustomer) } just runs
        //WHEN
        customerService.delete(fakeId)
        //THEN
        verify(exactly = 1) { customerRepository.findById(fakeId) }
        verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
    }
    private fun buildCustomer(
        firstName: String = "Eric",
        lastName: String = "Silva",
        cpf: String = "07400319450",
        email: String = "eric@email.com",
        password: String = "12345",
        zipCode: String = "54530120",
        street: String = "Rua Qualquer",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        id: Long = 1L
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street
        ),
        income = income,
        id = id
    )
}