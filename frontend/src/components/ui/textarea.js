import React from "react";
import { cn } from "lib/utils";

/**
 * 기본 다중 행 입력(Textarea) 컴포넌트
 * - Tailwind 스타일 기본 적용
 * - className, rows 등 모든 textarea 속성을 그대로 전달 가능
 */
export const Textarea = React.forwardRef(function Textarea(
    { className, rows = 4, ...props },
    ref
) {
    return (
        <textarea
            ref={ref}
            rows={rows}
            className={cn(
                "w-full rounded-md border border-gray-300 p-2 text-sm shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-y",
                className
            )}
            {...props}
        />
    );
});
