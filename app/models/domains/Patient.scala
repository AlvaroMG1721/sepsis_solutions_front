package models.domains

import java.util.Date
import javax.persistence._
import java.util.UUID

@Entity
@Table(name = "employees", schema = "KunderaExamples@cassandra_employees") //TODO
class Patient {

/*  @Id
  var id: String = UUID.randomUUID.toString()
  */
  @Column(name = PATIENTS_TABLE.ID_PACIENTE_COLUMN)
  var idPaciente: String = PATIENTS_TABLE.ID_PACIENTE_COLUMN

  @Column(name = PATIENTS_TABLE.SCORE_COLUMN)
  var score: String = PATIENTS_TABLE.SCORE_COLUMN

}
