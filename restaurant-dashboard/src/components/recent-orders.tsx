"use client"

import React from "react"
import { motion } from "framer-motion"
import { Badge } from "@/components/ui/badge"
import { ScrollArea } from "@/components/ui/scroll-area"

interface Order {
  id: string;
  customer?: {
    name: string;
  };
  orderDate?: string;
  totalAmount?: number;
  status: string;
  paymentMethod?: string;
}

interface RecentOrdersProps {
  orders: Order[];
}

const getStatusColor = (status: string) => {
  switch (status.toLowerCase()) {
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

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  })
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
          orders
            .filter(order => order && typeof order === 'object' && order.id && order.status)
            .map((order, index) => (
              <motion.div
                key={order.id}
                className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
                whileHover={{ scale: 1.01 }}
              >
                <div>
                  <h3 className="font-medium">Order #{order.id}</h3>
                  <p className="text-sm text-muted-foreground">
                    {order.customer?.name || 'Unknown Customer'} •
                    {order.orderDate ? formatDate(order.orderDate) : 'No date'}
                  </p>
                  <div className="flex gap-2 mt-1">
                    <Badge variant="outline" className={getStatusColor(order.status || 'pending')}>
                      {order.status || 'Pending'}
                    </Badge>
                    {order.paymentMethod && (
                      <Badge variant="outline" className="bg-blue-100 text-blue-800">
                        {order.paymentMethod}
                      </Badge>
                    )}
                  </div>
                </div>
                <div className="text-right">
                  <p className="font-medium">
                    ${order.totalAmount ? order.totalAmount.toFixed(2) : '0.00'}
                  </p>
                </div>
              </motion.div>
            ))
        )}
      </div>
    </ScrollArea>
  )
}
