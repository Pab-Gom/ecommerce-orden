package com.ecommerce.orden_service.assembler;

import com.ecommerce.orden_service.controller.OrdenControllerV2;
import com.ecommerce.orden_service.dto.OrdenResponseDto;
import com.ecommerce.orden_service.model.Orden;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class OrdenModelAssembler implements RepresentationModelAssembler<Orden, EntityModel<OrdenResponseDto>> {

    @Override
    public EntityModel<OrdenResponseDto> toModel(Orden orden) {
        OrdenResponseDto dto = mapToDTO(orden);

        return EntityModel.of(dto,
            linkTo(methodOn(OrdenControllerV2.class).obtenerPorId(orden.getId())).withSelfRel(),
            linkTo(methodOn(OrdenControllerV2.class).obtenerTodas()).withRel("ordenes"),
            linkTo(methodOn(OrdenControllerV2.class).obtenerMisOrdenes()).withRel("mis-ordenes"),
            linkTo(methodOn(OrdenControllerV2.class).obtenerPorEstado(orden.getEstado())).withRel("estado"),
            linkTo(methodOn(OrdenControllerV2.class).obtenerPorUsuario(orden.getUsuarioId())).withRel("usuario")
        );
    }

    public EntityModel<OrdenResponseDto> toModelFromDto(OrdenResponseDto dto) {
        return EntityModel.of(dto,
            linkTo(methodOn(OrdenControllerV2.class).obtenerPorId(dto.getId())).withSelfRel(),
            linkTo(methodOn(OrdenControllerV2.class).obtenerTodas()).withRel("ordenes"),
            linkTo(methodOn(OrdenControllerV2.class).obtenerMisOrdenes()).withRel("mis-ordenes"),
            linkTo(methodOn(OrdenControllerV2.class).obtenerPorEstado(dto.getEstado())).withRel("estado"),
            linkTo(methodOn(OrdenControllerV2.class).obtenerPorUsuario(dto.getUsuarioId())).withRel("usuario")
        );
    }

    @Override
    public CollectionModel<EntityModel<OrdenResponseDto>> toCollectionModel(Iterable<? extends Orden> entities) {
        CollectionModel<EntityModel<OrdenResponseDto>> models = RepresentationModelAssembler.super.toCollectionModel(entities);
        models.add(linkTo(methodOn(OrdenControllerV2.class).obtenerTodas()).withSelfRel());
        return models;
    }

    private OrdenResponseDto mapToDTO(Orden orden) {
        OrdenResponseDto dto = new OrdenResponseDto();
        dto.setId(orden.getId());
        dto.setUsuarioId(orden.getUsuarioId());
        dto.setTotal(orden.getTotal());
        dto.setEstado(orden.getEstado());
        dto.setFechaCreacion(orden.getFechaCreacion());
        dto.setItems(Collections.emptyList());
        return dto;
    }
}
