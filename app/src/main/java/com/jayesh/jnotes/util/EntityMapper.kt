package com.jayesh.jnotes.util

interface EntityMapper<Entity, DomainModel> {
    fun mapToDomain(entity: Entity): DomainModel
    fun mapFromDomain(domainModel: DomainModel): Entity
}