package com.lodbrock.tasker.util

import org.junit.Assert.*
import org.junit.Test

class TextUtilTest {

    @Test
    fun whenMaxNumberIsMinusOne(){
        assertEquals( "",
                TextUtil.threeDotLine("Hello world my name is", -1))
    }

    @Test
    fun whenTextIsShorterThanMaxNumber(){
        assertEquals( "He",
                TextUtil.threeDotLine("He", 10))

    }

    @Test
    fun whenTextIsLongerThanMaxNumber(){
        assertEquals( "He...",
                TextUtil.threeDotLine("Hello world my name is oo", 5))
    }


}