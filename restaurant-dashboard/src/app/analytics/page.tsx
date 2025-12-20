"use client"

import { useState, useEffect } from "react"
import { motion } from "framer-motion"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Sidebar } from "@/components/sidebar"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { RiPieChartLine, RiBarChartGroupedLine, RiCalendarLine } from "react-icons/ri"
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'
import { getApiUrl } from "@/lib/api"

interface AnalyticsData {
  menuAnalytics: {
    topSellingItems: Array<{ name: string; quantity: number; revenue: number }>
    categoryPerformance: Array<{ category: string; orders: number; revenue: number }>
    timeBasedAnalysis: {
      peakHours: Array<{ hour: string; orders: number }>
      weeklyTrends: Array<{ day: string; orders: number; revenue: number }>
    }
  }
  customerAnalytics: {
    loyaltyDistribution: Array<{ category: string; count: number }>
    orderFrequency: Array<{ frequency: string; customers: number }>
    averageOrderValue: number
  }
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8']

export default function AnalyticsPage() {
  const [data, setData] = useState<AnalyticsData | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchAnalyticsData()
  }, [])

  const fetchAnalyticsData = async () => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) return
      const { id } = JSON.parse(adminData)

      const response = await fetch(getApiUrl("/reports/detailed"), {
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString()
        }
      })

      if (response.ok) {
        const analyticsData = await response.json()
        setData(analyticsData)
      }
    } catch (error) {
      console.error('Failed to fetch analytics data:', error)
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <div>Loading...</div>

  return (
    <div className="flex">
      <Sidebar />
      <div className="flex-1 min-h-screen p-8 bg-background">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="max-w-7xl mx-auto space-y-8"
        >
          <div>
            <h1 className="text-3xl font-semibold">Advanced Analytics</h1>
            <p className="text-muted-foreground">Detailed insights into your restaurant's performance</p>
          </div>

          <Tabs defaultValue="menu" className="space-y-6">
            <TabsList>
              <TabsTrigger value="menu" className="flex items-center gap-2">
                <RiPieChartLine /> Menu Analysis
              </TabsTrigger>
              <TabsTrigger value="customers" className="flex items-center gap-2">
                <RiBarChartGroupedLine /> Customer Insights
              </TabsTrigger>
              <TabsTrigger value="timing" className="flex items-center gap-2">
                <RiCalendarLine /> Timing Patterns
              </TabsTrigger>
            </TabsList>

            <TabsContent value="menu" className="space-y-6">
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <Card>
                  <CardHeader>
                    <CardTitle>Top Selling Items</CardTitle>
                  </CardHeader>
                  <CardContent className="h-[300px]">
                    <ResponsiveContainer width="100%" height="100%">
                      <PieChart>
                        <Pie
                          data={data?.menuAnalytics.topSellingItems}
                          dataKey="quantity"
                          nameKey="name"
                          label
                        >
                          {data?.menuAnalytics.topSellingItems.map((_, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                          ))}
                        </Pie>
                        <Tooltip />
                      </PieChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader>
                    <CardTitle>Category Performance</CardTitle>
                  </CardHeader>
                  <CardContent className="h-[300px]">
                    <ResponsiveContainer width="100%" height="100%">
                      <BarChart data={data?.menuAnalytics.categoryPerformance}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="category" />
                        <YAxis />
                        <Tooltip />
                        <Bar dataKey="orders" fill="#8884d8" />
                        <Bar dataKey="revenue" fill="#82ca9d" />
                      </BarChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>
              </div>
            </TabsContent>

            <TabsContent value="customers" className="space-y-6">
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <Card>
                  <CardHeader>
                    <CardTitle>Customer Loyalty Distribution</CardTitle>
                  </CardHeader>
                  <CardContent className="h-[300px]">
                    <ResponsiveContainer width="100%" height="100%">
                      <PieChart>
                        <Pie
                          data={data?.customerAnalytics.loyaltyDistribution}
                          dataKey="count"
                          nameKey="category"
                          label
                        >
                          {data?.customerAnalytics.loyaltyDistribution.map((_, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                          ))}
                        </Pie>
                        <Tooltip />
                      </PieChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader>
                    <CardTitle>Order Frequency</CardTitle>
                  </CardHeader>
                  <CardContent className="h-[300px]">
                    <ResponsiveContainer width="100%" height="100%">
                      <BarChart data={data?.customerAnalytics.orderFrequency}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="frequency" />
                        <YAxis />
                        <Tooltip />
                        <Bar dataKey="customers" fill="#8884d8" />
                      </BarChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>
              </div>
            </TabsContent>

            <TabsContent value="timing" className="space-y-6">
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <Card>
                  <CardHeader>
                    <CardTitle>Peak Hours Analysis</CardTitle>
                  </CardHeader>
                  <CardContent className="h-[300px]">
                    <ResponsiveContainer width="100%" height="100%">
                      <BarChart data={data?.menuAnalytics.timeBasedAnalysis.peakHours}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="hour" />
                        <YAxis />
                        <Tooltip />
                        <Bar dataKey="orders" fill="#8884d8" />
                      </BarChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader>
                    <CardTitle>Weekly Trends</CardTitle>
                  </CardHeader>
                  <CardContent className="h-[300px]">
                    <ResponsiveContainer width="100%" height="100%">
                      <BarChart data={data?.menuAnalytics.timeBasedAnalysis.weeklyTrends}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="day" />
                        <YAxis />
                        <Tooltip />
                        <Bar dataKey="orders" fill="#8884d8" />
                        <Bar dataKey="revenue" fill="#82ca9d" />
                      </BarChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>
              </div>
            </TabsContent>
          </Tabs>
        </motion.div>
      </div>
    </div>
  )
}