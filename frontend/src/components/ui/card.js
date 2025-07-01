import React from "react";
import { cn } from "@/lib/utils";

export function Card({ className, ...props }) {
    return (
        <div
            className={cn("rounded-lg border bg-white p-4 shadow-sm", className)}
            {...props}
        />
    );
}

export function CardHeader({ className, ...props }) {
    return (
        <div className={cn("mb-2 text-lg font-semibold", className)} {...props} />
    );
}

export function CardContent({ className, ...props }) {
    return (
        <div className={cn("text-sm text-gray-700", className)} {...props} />
    );
}
