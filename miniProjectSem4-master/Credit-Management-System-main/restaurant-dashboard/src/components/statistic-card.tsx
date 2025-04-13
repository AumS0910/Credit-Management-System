"use client"

import type React from "react"
import { Card, CardContent } from "@/components/ui/card"
import { RiArrowUpSLine, RiArrowDownSLine } from "react-icons/ri"

interface StatisticCardProps {
  title: string
  value: string
  description: string
  icon: React.ReactNode
  trend?: "up" | "down" | "neutral"
}

export function StatisticCard({
  title,
  value,
  description,
  icon,
  trend = "neutral",
}: StatisticCardProps) {
  return (
    <Card className="glass-card overflow-hidden">
      <CardContent className="p-6">
        <div className="flex items-center justify-between">
          <div className="space-y-1">
            <p className="text-sm font-medium text-muted-foreground">{title}</p>
            <div className="flex items-baseline">
              <h3 className="text-2xl font-semibold">{value}</h3>
            </div>
          </div>
          <div className="h-9 w-9 rounded-md bg-primary/10 flex items-center justify-center text-primary">
            {icon}
          </div>
        </div>
        <div className="mt-2 flex items-center gap-1 text-xs">
          {trend === "up" && (
            <RiArrowUpSLine className="h-4 w-4 text-emerald-500" />
          )}
          {trend === "down" && (
            <RiArrowDownSLine className="h-4 w-4 text-rose-500" />
          )}
          <span
            className={
              trend === "up"
                ? "text-emerald-500"
                : trend === "down"
                ? "text-rose-500"
                : "text-muted-foreground"
            }
          >
            {description}
          </span>
        </div>
      </CardContent>
    </Card>
  )
}
