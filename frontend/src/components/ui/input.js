// src/components/ui/input.js

import React from "react";
import { cn } from "@/lib/utils";

/**
 * Input 컴포넌트 - 기본 텍스트 입력 필드
 * - className, ...props로 외부 스타일 및 속성 확장 가능
 */
export const Input = React.forwardRef(function Input({ className, ...props }, ref) {
    return (
        <input
            ref={ref}
            className={cn(
                "w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent",
                className
            )}
            {...props}
        />
    );
});
