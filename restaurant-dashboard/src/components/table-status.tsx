"use client"

import React from "react"
import { motion } from "framer-motion"
import { Badge } from "@/components/ui/badge"
import { ScrollArea } from "@/components/ui/scroll-area"

type TableStatus = "occupied" | "available" | "reserved"

interface Table {
  id: number
  status: TableStatus
  capacity: number
  occupiedSince?: string
  reservedFor?: string
  reservationTime?: string
}

const tables: Table[] = [
  {
    id: 1,
    status: "occupied",
    capacity: 4,
    occupiedSince: "1:30 PM",
  },
  {
    id: 2,
    status: "available",
    capacity: 2,
  },
  {
    id: 3,
    status: "occupied",
    capacity: 6,
    occupiedSince: "2:15 PM",
  },
  {
    id: 4,
    status: "available",
    capacity: 4,
  },
  {
    id: 5,
    status: "reserved",
    capacity: 4,
    reservedFor: "Johnson",
    reservationTime: "7:00 PM",
  },
  {
    id: 6,
    status: "occupied",
    capacity: 2,
    occupiedSince: "12:45 PM",
  },
  {
    id: 7,
    status: "reserved",
    capacity: 6,
    reservedFor: "Martinez",
    reservationTime: "8:30 PM",
  },
  {
    id: 8,
    status: "available",
    capacity: 8,
  },
]

const getStatusColor = (status: TableStatus): string => {
  switch (status) {
    case "occupied":
      return "bg-rose-500/10 text-rose-500"
    case "available":
      return "bg-emerald-500/10 text-emerald-500"
    case "reserved":
      return "bg-amber-500/10 text-amber-500"
    default:
      return "bg-gray-500/10 text-gray-500"
  }
}

const getCapacityLabel = (capacity: number): string => {
  return `${capacity} ${capacity === 1 ? "person" : "people"}`
}

export function TableStatus() {
  return (
    <ScrollArea className="h-[400px] pr-4">
      <div className="grid grid-cols-1 gap-3">
        {tables.map((table, index) => (
          <motion.div
            key={table.id}
            className="flex items-center justify-between rounded-lg border p-3"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.05 }}
            whileHover={{ scale: 1.02 }}
          >
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-md bg-primary/10 font-medium">
                {table.id}
              </div>
              <div>
                <div className="font-medium">Table {table.id}</div>
                <div className="text-xs text-muted-foreground">
                  {getCapacityLabel(table.capacity)}
                </div>
              </div>
            </div>
            <div className="flex items-center gap-3">
              {table.status === "occupied" && (
                <div className="text-right text-xs text-muted-foreground">
                  Since {table.occupiedSince}
                </div>
              )}
              {table.status === "reserved" && (
                <div className="text-right text-xs text-muted-foreground">
                  {table.reservedFor} • {table.reservationTime}
                </div>
              )}
              <Badge className={getStatusColor(table.status)}>
                {table.status}
              </Badge>
            </div>
          </motion.div>
        ))}
      </div>
    </ScrollArea>
  )
}
