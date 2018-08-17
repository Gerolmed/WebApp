package edu.hm.cs.projektstudium.findlunch.webapp.service;

/**
 * Interface for services related to the hibernate service.
 */
public interface HibernateService {

    <T> T initializeAndUnproxy(T entity);
}
