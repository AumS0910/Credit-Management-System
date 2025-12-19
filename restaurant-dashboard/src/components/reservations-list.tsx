"use client"

import React from "react"
import { motion } from "framer-motion"
import { ScrollArea } from "@/components/ui/scroll-area"

interface Reservation {
  id: string
  name: string
  guests: number
  dateDisplay: string
  time: string
  phone: string
  specialRequests?: string
  status: "confirmed" | "pending" | "cancelled"
}

const upcomingReservations: Reservation[] = [
  {
    id: "RES-1452",
    name: "Emma Wilson",
    guests: 4,
    dateDisplay: "April 15, 2025",
    time: "7:30 PM",
    phone: "(555) 123-4567",
    specialRequests: "Window table preferred",
    status: "confirmed",
  },
  {
    id: "RES-1453",
    name: "James Rodriguez",
    guests: 2,
    dateDisplay: "April 15, 2025",
    time: "8:00 PM",
    phone: "(555) 987-6543",
    status: "confirmed",
  },
  {
    id: "RES-1455",
    name: "Olivia Smith",
    guests: 6,
    dateDisplay: "April 15, 2025",
    time: "6:45 PM",
    phone: "(555) 222-3333",
    specialRequests: "Anniversary celebration",
    status: "confirmed",
  },
  {
    id: "RES-1456",
    name: "Noah Johnson",
    guests: 3,
    dateDisplay: "April 16, 2025",
    time: "7:00 PM",
    phone: "(555) 444-5555",
    status: "pending",
  },
  {
    id: "RES-1457",
    name: "Sophia Brown",
    guests: 8,
    dateDisplay: "April 16, 2025",
    time: "8:15 PM",
    phone: "(555) 777-8888",
    specialRequests: "Birthday celebration, cake provided",
    status: "confirmed",
  },
]

export function ReservationsList() {
  return (
    <ScrollArea className="h-[400px] pr-4">
      <div className="space-y-4">
        {upcomingReservations.map((reservation, index) => (
          <motion.div
            key={reservation.id}
            className="flex flex-col space-y-2 rounded-lg border p-4"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
            whileHover={{ scale: 1.01 }}
          >
            <div className="flex justify-between">
              <div>
                <h4 className="font-medium">{reservation.name}</h4>
                <p className="text-xs text-muted-foreground">
                  {reservation.dateDisplay} • {reservation.time}
                </p>
              </div>
              <div className="text-right">
                <span className="rounded-full bg-primary/10 px-2 py-1 text-xs font-medium text-primary">
                  {reservation.guests} {reservation.guests === 1 ? "Guest" : "Guests"}
                </span>
              </div>
            </div>
            <div className="flex items-center justify-between">
              <p className="text-xs text-muted-foreground">{reservation.phone}</p>
              {reservation.status === "confirmed" ? (
                <span className="text-xs font-medium text-emerald-500">Confirmed</span>
              ) : reservation.status === "pending" ? (
                <span className="text-xs font-medium text-amber-500">Pending</span>
              ) : (
                <span className="text-xs font-medium text-rose-500">Cancelled</span>
              )}
            </div>
            {reservation.specialRequests && (
              <p className="text-xs italic text-muted-foreground">
                "{reservation.specialRequests}"
              </p>
            )}
          </motion.div>
        ))}
      </div>
    </ScrollArea>
  )
}
