"use client"

import React from "react"
import { motion } from "framer-motion"
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ChartEvent,
  type TooltipItem,
} from "chart.js"
import { Bar } from "react-chartjs-2"

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
)

interface MenuItem {
  name: string
  category: string
  orderCount: number
  revenue: number
}

const popularItems: MenuItem[] = [
  {
    name: "Grilled Salmon",
    category: "Main Course",
    orderCount: 124,
    revenue: 2604,
  },
  {
    name: "Beef Burger",
    category: "Main Course",
    orderCount: 98,
    revenue: 1372,
  },
  {
    name: "Chocolate Cake",
    category: "Dessert",
    orderCount: 87,
    revenue: 783,
  },
  {
    name: "Caesar Salad",
    category: "Starter",
    orderCount: 76,
    revenue: 836,
  },
  {
    name: "Margherita Pizza",
    category: "Main Course",
    orderCount: 73,
    revenue: 1022,
  },
  {
    name: "Tiramisu",
    category: "Dessert",
    orderCount: 56,
    revenue: 504,
  },
]

export function PopularItems() {
  const chartData = {
    labels: popularItems.map((item) => item.name),
    datasets: [
      {
        label: "Orders",
        data: popularItems.map((item) => item.orderCount),
        backgroundColor: "rgba(45, 212, 191, 0.5)",
        borderColor: "rgba(45, 212, 191, 1)",
        borderWidth: 1,
        borderRadius: 5,
      },
    ],
  }

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        backgroundColor: "rgba(0, 0, 0, 0.7)",
        padding: 10,
        caretSize: 5,
        displayColors: false,
        callbacks: {
          label: (context: TooltipItem<"bar">) => `Orders: ${context.raw}`,
          title: (tooltipItems: TooltipItem<"bar">[]) => tooltipItems[0].label,
        },
      },
    },
    scales: {
      x: {
        ticks: {
          maxRotation: 45,
          minRotation: 45,
          font: {
            size: 10,
          },
        },
        grid: {
          display: false,
        },
      },
      y: {
        beginAtZero: true,
        grid: {
          color: "rgba(255, 255, 255, 0.1)",
        },
      },
    },
  }

  return (
    <div className="space-y-4">
      <motion.div
        className="h-[200px] w-full"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <Bar data={chartData} options={chartOptions} />
      </motion.div>
      <div className="space-y-2">
        {popularItems.slice(0, 3).map((item, index) => (
          <motion.div
            key={item.name}
            className="flex items-center justify-between rounded-lg border p-3"
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.2 + index * 0.1 }}
          >
            <div>
              <div className="font-medium">{item.name}</div>
              <div className="text-xs text-muted-foreground">{item.category}</div>
            </div>
            <div className="text-right">
              <div className="font-medium">{item.orderCount} orders</div>
              <div className="text-xs text-emerald-500">${item.revenue}</div>
            </div>
          </motion.div>
        ))}
      </div>
    </div>
  )
}
