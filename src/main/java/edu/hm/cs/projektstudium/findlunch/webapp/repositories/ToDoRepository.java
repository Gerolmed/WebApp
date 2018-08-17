package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import edu.hm.cs.projektstudium.findlunch.webapp.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * The Interface ToDoRepository. Abstraction for the data access layer.
 */
@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Serializable> {

	/**
	 * Finds list of todo's of a sales person by email.
	 * @param email email address of the sales person
	 * @return List of todo's
	 */
    List<ToDo> findBySalesPersonEmail(String email);

    /**
     * Finds a todo by id.
     * @param toDoId Id of the todo
     * @return the todo
     */
    ToDo findById(int toDoId);

    /**
	 * Finds list of todo's of a sales person by offer id.
	 * @param offerId the offer Id
	 * @return List of todo's
	 */
    List<ToDo> findByOfferId(int offerId);

    @Modifying
    @Query("delete from ToDo where id = ?1")
    void deleteById(int toDoId);

}
