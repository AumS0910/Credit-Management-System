"use client"

import React from "react"
import { motion } from "framer-motion"
import { Badge } from "@/components/ui/badge"
import { ScrollArea } from "@/components/ui/scroll-area"

type OrderStatus = "completed" | "in-progress" | "pending" | "cancelled"

interface Order {
  id: string;
  customer: string;
  items: string[];
  total: number;
  status: OrderStatus;
  time: string;
  table?: string;
}

interface RecentOrdersProps {
  orders: Order[];
}

const getStatusColor = (status: OrderStatus) => {
  switch (status) {
    case "completed":
      return "bg-emerald-500/10 text-emerald-500 hover:bg-emerald-500/20"
    case "in-progress":
      return "bg-amber-500/10 text-amber-500 hover:bg-amber-500/20"
    case "pending":
      return "bg-blue-500/10 text-blue-500 hover:bg-blue-500/20"
    case "cancelled":
      return "bg-rose-500/10 text-rose-500 hover:bg-rose-500/20"
    default:
      return "bg-gray-500/10 text-gray-500 hover:bg-gray-500/20"
  }
}

export function RecentOrders({ orders }: RecentOrdersProps) {
  return (
    <ScrollArea className="h-[400px] pr-4">
      <div className="space-y-4">
        {orders.length === 0 ? (
          <div className="text-center text-muted-foreground py-8">
            No recent orders
          </div>
        ) : (
          orders.map((order, index) => (
            <motion.div
              key={order.id}
              className="flex flex-col space-y-3 rounded-lg border p-4"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
            >
              <div className="flex items-start justify-between">
                <div>
                  <div className="flex items-center gap-2">
                    <h4 className="font-semibold">{order.id}</h4>
                    <Badge variant="outline" className={getStatusColor(order.status)}>
                      {order.status.replace("-", " ")}
                    </Badge>
                  </div>
                  <p className="text-sm text-muted-foreground">
                    {order.customer} {order.table && `• ${order.table}`}
                  </p>
                </div>
                <div className="text-right">
                  <p className="font-medium">${order.total.toFixed(2)}</p>
                  <p className="text-sm text-muted-foreground">
                    {new Date(order.time).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </p>
                </div>
              </div>
              <div>
                <p className="text-sm text-muted-foreground line-clamp-1">
                  {order.items.join(", ")}
                </p>
              </div>
            </motion.div>
          ))
        )}
      </div>
    </ScrollArea>
  )
}
