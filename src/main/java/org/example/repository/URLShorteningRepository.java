package org.example.repository;

import org.example.model.Url;

public interface URLShorteningRepository {
    void saveOrUpdate(Url Url);
    Object findUrl(String key);
    void delete(String key);

}