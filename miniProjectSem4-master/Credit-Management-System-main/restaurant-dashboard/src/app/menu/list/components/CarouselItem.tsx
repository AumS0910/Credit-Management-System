import { useSpring, animated, config } from '@react-spring/web'
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { RiEditLine, RiDeleteBinLine, RiStarFill } from "react-icons/ri"
import { motion } from "framer-motion"

interface CarouselItemProps {
  item: any;
  position: {
    x: number;
    z: number;
    scale: number;
  };
  onClick: () => void;
  onSelect: () => void;
  onEdit: () => void;
  onDelete: () => void;
  isSelected: boolean;
}

export function CarouselItem({
  item,
  position,
  onClick,
  onSelect,
  onEdit,
  onDelete,
  isSelected
}: CarouselItemProps) {
  const springs = useSpring({
    to: {
      transform: `translate3d(${position.x}px, 0, ${position.z}px) scale(${position.scale})`,
      opacity: position.z < 0 ? 0 : 1,
      zIndex: position.z < 0 ? -1 : 1,
    },
    config: config.gentle,
  })

  return (
    <animated.div
      style={{
        ...springs,
        position: 'absolute',
        left: '50%',
        transform: 'translateX(-50%)',
        transformStyle: 'preserve-3d',
        width: '100%',
        maxWidth: '800px',
      }}
      onClick={onClick}
    >
      <Card className="bg-white rounded-xl p-6">
        <div className="flex gap-8">
          {/* Main Image */}
          <div className="relative w-[300px] h-[300px] rounded-full overflow-hidden">
            <img
              src={item.imageUrl || '/placeholder.jpg'}
              alt={item.name}
              className="w-full h-full object-cover"
            />
          </div>

          {/* Content */}
          <div className="flex-1">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h2 className="text-3xl font-bold mb-2">{item.name}</h2>
                <p className="text-gray-600 text-lg">{item.description}</p>
              </div>
              <div className="flex items-center gap-2">
                <span className="text-2xl font-bold">{item.rating || 4.9}</span>
                <RiStarFill className="text-yellow-400 text-2xl" />
              </div>
            </div>

            {/* Price and Actions */}
            <div className="flex justify-between items-center mt-8">
              <span className="text-2xl font-bold">${item.price}</span>
              <div className="flex gap-3">
                <Button 
                  size="lg" 
                  variant="outline"
                  onClick={(e) => {
                    e.stopPropagation();
                    onEdit();
                  }}
                >
                  <RiEditLine className="w-5 h-5" />
                </Button>
                <Button 
                  size="lg" 
                  variant="outline"
                  onClick={(e) => {
                    e.stopPropagation();
                    onDelete();
                  }}
                >
                  <RiDeleteBinLine className="w-5 h-5" />
                </Button>
              </div>
            </div>
          </div>
        </div>

        {/* Thumbnail Navigation */}
        <div className="flex justify-center gap-4 mt-8">
          {[1, 2, 3, 4].map((_, index) => (
            <button
              key={index}
              className={`w-12 h-12 rounded-full overflow-hidden border-2 
                ${index === 0 ? 'border-primary' : 'border-transparent'}`}
            >
              <img
                src={item.imageUrl || '/placeholder.jpg'}
                alt={`${item.name} thumbnail`}
                className="w-full h-full object-cover"
              />
            </button>
          ))}
        </div>
      </Card>
    </animated.div>
  )
}

interface CarouselItemProps {
  item: any;
  isActive: boolean;
  onClick: () => void;
}

export function CarouselItem({
  item,
  isActive,
  onClick
}: CarouselItemProps) {
  return (
    <motion.button
      className={`thumbnail ${isActive ? 'active' : ''}`}
      onClick={onClick}
      whileHover={{ scale: 1.1 }}
    >
      <img 
        src={item.imageUrl || '/placeholder.jpg'} 
        alt={item.name} 
        className="w-full h-full object-cover"
      />
    </motion.button>
  )
}