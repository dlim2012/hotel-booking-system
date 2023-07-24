package com.dlim2012.clients.entity;

import java.time.LocalDateTime;

public abstract class BookingEntity {

    protected Long id;

    protected Integer userId; // Foreign key to User ID

    protected Integer roomId;

    protected Integer hotelId;

    protected LocalDateTime startDateTime;

    protected LocalDateTime endDateTime;

    protected Integer quantity;

    protected BookingStatus status;

    protected Long priceInCents;

    protected String invoiceId; // Invoice.id without foreign key constraint

    protected LocalDateTime invoiceConfirmTime;
}
